package org.jasig.cas.services;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.util.services.RegisteredServiceJsonSerializer;

/**
 * RegisteredService implementation with id and service_body which represent the service with json pattern 
 * @see RegisteredServiceJsonSerializer
 * @see RegexRegisteredService
 */
@Entity
@Table(name = "JsonRegisteredService")
public class JsonRegisteredService implements RegisteredService , Comparable<RegisteredService>{

	private static final long serialVersionUID = -6671407600580319676L;
	
	public static JsonRegisteredService toJsonRegisteredService(RegisteredService registeredService) {
		assert registeredService != null;
		if(registeredService instanceof JsonRegisteredService) {
			return (JsonRegisteredService) registeredService;
		} else {
			return new JsonRegisteredService(registeredService);
		}
	}
	
	public JsonRegisteredService() {
	}
	
	private JsonRegisteredService(RegisteredService internalRegisteredService) {
		this.internalRegisteredService = internalRegisteredService;
		getServiceBody();
	}
	
	@Transient
	private RegisteredService internalRegisteredService;
	
	/**
	 * init and get internal RegisteredService which parsed from 'serviceBody' property
	 * ,if serviceBody is empty use RegexRegisteredService as a default service 
	 * @return
	 */
	private RegisteredService getInternalRegisteredService(){
		if(internalRegisteredService == null){
			if(!StringUtils.isBlank(serviceBody)){
				//FIXME =>impl- RegisteredServiceJsonSerializer
				internalRegisteredService = new RegisteredServiceJsonSerializer()
						.fromJson(new StringReader(serviceBody));
			} else {
				RegexRegisteredService service = new RegexRegisteredService();
				service.setServiceId(".*");//we dont have any setter like 'setServicePattern'
				internalRegisteredService = service;
			}
		}
		if (internalRegisteredService == null) {
			throw new IllegalStateException("Not enough info to initialize the internalRegisteredService");
		}
		return internalRegisteredService;
	}

	@Id
	private long id;

	@Column(name = "service_body")
	private String serviceBody;

	@Override
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getServiceBody() {
		if(serviceBody == null) {
			StringWriter stringWriter = new StringWriter();
			new RegisteredServiceJsonSerializer().toJson(stringWriter, internalRegisteredService);
			serviceBody = stringWriter.toString();
		}
		return serviceBody;
	}

	public void setServiceBody(String serviceBody) {
		this.serviceBody = serviceBody;
	}

	@Override
	public RegisteredServiceProxyPolicy getProxyPolicy() {
		return getInternalRegisteredService().getProxyPolicy();
	}

	@Override
	public String getServiceId() {
		return getInternalRegisteredService().getServiceId();
	}

	@Override
	public String getName() {
		return getInternalRegisteredService().getName();
	}

	@Override
	public String getTheme() {
		return getInternalRegisteredService().getTheme();
	}

	@Override
	public String getDescription() {
		return getInternalRegisteredService().getDescription();
	}

	@Override
	public int getEvaluationOrder() {
		return getInternalRegisteredService().getEvaluationOrder();
	}

	@Override
	public void setEvaluationOrder(int evaluationOrder) {
		getInternalRegisteredService().setEvaluationOrder(evaluationOrder);
		//TODO update 
	}

	@Override
	public RegisteredServiceUsernameAttributeProvider getUsernameAttributeProvider() {
		return getInternalRegisteredService().getUsernameAttributeProvider();
	}

	@Override
	public Set<String> getRequiredHandlers() {
		return getInternalRegisteredService().getRequiredHandlers();
	}

	@Override
	public RegisteredServiceAccessStrategy getAccessStrategy() {
		return getInternalRegisteredService().getAccessStrategy();
	}

	@Override
	public boolean matches(Service service) {
		return getInternalRegisteredService().matches(service);
	}

	@Override
	public RegisteredService clone() throws CloneNotSupportedException {
		JsonRegisteredService cloneJsonRegisteredService = 
				JsonRegisteredService.toJsonRegisteredService(getInternalRegisteredService().clone());
		cloneJsonRegisteredService.setId(getId());
		return cloneJsonRegisteredService;
		//throw new CloneNotSupportedException("Cannot Clone JsonRegisteredService");
	}

	@Override
	public LogoutType getLogoutType() {
		return getInternalRegisteredService().getLogoutType();
	}

	@Override
	public RegisteredServiceAttributeReleasePolicy getAttributeReleasePolicy() {
		return getInternalRegisteredService().getAttributeReleasePolicy();
	}

	@Override
	public URL getLogo() {
		return getInternalRegisteredService().getLogo();
	}

	@Override
	public URL getLogoutUrl() {
		return getInternalRegisteredService().getLogoutUrl();
	}

	@Override
	public RegisteredServicePublicKey getPublicKey() {
		return getInternalRegisteredService().getPublicKey();
	}

	@Override
	public Map<String, RegisteredServiceProperty> getProperties() {
		return getInternalRegisteredService().getProperties();
	}

	
 @Override
    public int compareTo(final RegisteredService other) {
        return new CompareToBuilder()
                  .append(this.getEvaluationOrder(), other.getEvaluationOrder())
                  .append(this.getName().toLowerCase(), other.getName().toLowerCase())
                  .append(this.getServiceId(), other.getServiceId())
                  .toComparison();
    }
}
