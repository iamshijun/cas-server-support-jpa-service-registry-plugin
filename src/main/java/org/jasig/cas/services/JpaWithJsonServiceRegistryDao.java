package org.jasig.cas.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jasig.cas.services.JsonServiceRegistryDao;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServiceRegistryDao;
import org.springframework.transaction.annotation.Transactional;

/**
 * ServiceRegistryDao based on  {@link JsonRegisteredService}
 * @see JsonServiceRegistryDao
 *
 */
public class JpaWithJsonServiceRegistryDao implements ServiceRegistryDao{

	@PersistenceContext(unitName = "serviceEntityManagerFactory")
    private EntityManager entityManager;

	
	@Override
	@Transactional(transactionManager = "transactionManagerServiceReg", readOnly = false)
	public RegisteredService save(RegisteredService registeredService) {
		//JsonRegisteredService
		final boolean isNew = registeredService.getId() == RegisteredService.INITIAL_IDENTIFIER_VALUE;
		final RegisteredService r = this.entityManager.merge(
							JsonRegisteredService.toJsonRegisteredService(registeredService));

		if (!isNew) {
			this.entityManager.persist(r);
		}

		return r;

	}

	@Override
	@Transactional(transactionManager = "transactionManagerServiceReg", readOnly = false)
    public boolean delete(final RegisteredService registeredService) {
		
		JsonRegisteredService jsonRegisteredService;
		if(registeredService instanceof JsonRegisteredService) {
			jsonRegisteredService = (JsonRegisteredService) registeredService;
		}else {
			jsonRegisteredService =
					this.entityManager.find(JsonRegisteredService.class,registeredService.getId());
		}
		 
        if (this.entityManager.contains(jsonRegisteredService)) {
            this.entityManager.remove(jsonRegisteredService);
        } else {
        	JsonRegisteredService queryJsonRegisteredService = this.entityManager.merge(jsonRegisteredService);
            this.entityManager.remove(queryJsonRegisteredService);
        }
        return true;
    }

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" }) 
	public List<RegisteredService> load() {
		  return (List)this.entityManager.createQuery("select r from JsonRegisteredService r", JsonRegisteredService.class)
	                .getResultList();
	}

	@Override
	public RegisteredService findServiceById(long id) {
		return this.entityManager.find(JsonRegisteredService.class,id);
	}

}
