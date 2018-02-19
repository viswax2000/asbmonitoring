//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.01.20 at 06:25:51 PM CET 
//


package com.vcc.asb.config.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.Duration;
import org.joda.time.DateTime;


/**
 * <p>Java class for SubscriptionDescription complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SubscriptionDescription">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schemas.microsoft.com/netservices/2010/10/servicebus/connect}Resource">
 *       &lt;sequence>
 *         &lt;element name="TopicName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LockDuration" type="{http://schemas.microsoft.com/2003/10/Serialization/}duration" minOccurs="0"/>
 *         &lt;element name="RequiresSession" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="DefaultMessageTimeToLive" type="{http://schemas.microsoft.com/2003/10/Serialization/}duration" minOccurs="0"/>
 *         &lt;element name="DeadLetteringOnMessageExpiration" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="DeadLetteringOnFilterEvaluationExceptions" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="DefaultRuleDescription" type="{http://schemas.microsoft.com/netservices/2010/10/servicebus/connect}RuleDescription" minOccurs="0"/>
 *         &lt;element name="MessageCount" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="MaxDeliveryCount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="EnableBatchedOperations" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="Status" type="{http://schemas.microsoft.com/netservices/2010/10/servicebus/connect}EntityStatus" minOccurs="0"/>
 *         &lt;element name="ForwardTo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AccessedAt" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="UserMetadata" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CountDetails" type="{http://schemas.microsoft.com/netservices/2011/06/servicebus}MessageCountDetails" minOccurs="0"/>
 *         &lt;element name="AutoDeleteOnIdle" type="{http://schemas.microsoft.com/2003/10/Serialization/}duration" minOccurs="0"/>
 *         &lt;element name="EntityAvailabilityStatus" type="{http://schemas.microsoft.com/netservices/2010/10/servicebus/connect}EntityAvailabilityStatus" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubscriptionDescription", propOrder = {
    "topicName",
    "lockDuration",
    "requiresSession",
    "defaultMessageTimeToLive",
    "deadLetteringOnMessageExpiration",
    "deadLetteringOnFilterEvaluationExceptions",
    "defaultRuleDescription",
    "messageCount",
    "maxDeliveryCount",
    "enableBatchedOperations",
    "status",
    "forwardTo",
    "accessedAt",
    "userMetadata",
    "countDetails",
    "autoDeleteOnIdle",
    "entityAvailabilityStatus"
})
@XmlRootElement(name = "SubscriptionDescription")
public class SubscriptionDescription
    extends Resource
{

    @XmlElement(name = "TopicName")
    protected String topicName;
    @XmlElement(name = "LockDuration")
    protected Duration lockDuration;
    @XmlElement(name = "RequiresSession")
    protected Boolean requiresSession;
    @XmlElement(name = "DefaultMessageTimeToLive")
    protected Duration defaultMessageTimeToLive;
    @XmlElement(name = "DeadLetteringOnMessageExpiration")
    protected Boolean deadLetteringOnMessageExpiration;
    @XmlElement(name = "DeadLetteringOnFilterEvaluationExceptions")
    protected Boolean deadLetteringOnFilterEvaluationExceptions;
    @XmlElement(name = "DefaultRuleDescription")
    protected RuleDescription defaultRuleDescription;
    @XmlElement(name = "MessageCount")
    protected Long messageCount;
    @XmlElement(name = "MaxDeliveryCount")
    protected Integer maxDeliveryCount;
    @XmlElement(name = "EnableBatchedOperations")
    protected Boolean enableBatchedOperations;
    @XmlElement(name = "Status")
    protected EntityStatus status;
    @XmlElement(name = "ForwardTo")
    protected String forwardTo;
    @XmlElement(name = "AccessedAt", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected DateTime accessedAt;
    @XmlElement(name = "UserMetadata")
    protected String userMetadata;
    @XmlElement(name = "CountDetails")
    protected MessageCountDetails countDetails;
    @XmlElement(name = "AutoDeleteOnIdle")
    protected Duration autoDeleteOnIdle;
    @XmlElement(name = "EntityAvailabilityStatus")
    protected EntityAvailabilityStatus entityAvailabilityStatus;

    /**
     * Gets the value of the topicName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTopicName() {
        return topicName;
    }

    /**
     * Sets the value of the topicName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTopicName(String value) {
        this.topicName = value;
    }

    /**
     * Gets the value of the lockDuration property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getLockDuration() {
        return lockDuration;
    }

    /**
     * Sets the value of the lockDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setLockDuration(Duration value) {
        this.lockDuration = value;
    }

    /**
     * Gets the value of the requiresSession property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRequiresSession() {
        return requiresSession;
    }

    /**
     * Sets the value of the requiresSession property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRequiresSession(Boolean value) {
        this.requiresSession = value;
    }

    /**
     * Gets the value of the defaultMessageTimeToLive property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getDefaultMessageTimeToLive() {
        return defaultMessageTimeToLive;
    }

    /**
     * Sets the value of the defaultMessageTimeToLive property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setDefaultMessageTimeToLive(Duration value) {
        this.defaultMessageTimeToLive = value;
    }

    /**
     * Gets the value of the deadLetteringOnMessageExpiration property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDeadLetteringOnMessageExpiration() {
        return deadLetteringOnMessageExpiration;
    }

    /**
     * Sets the value of the deadLetteringOnMessageExpiration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDeadLetteringOnMessageExpiration(Boolean value) {
        this.deadLetteringOnMessageExpiration = value;
    }

    /**
     * Gets the value of the deadLetteringOnFilterEvaluationExceptions property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDeadLetteringOnFilterEvaluationExceptions() {
        return deadLetteringOnFilterEvaluationExceptions;
    }

    /**
     * Sets the value of the deadLetteringOnFilterEvaluationExceptions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDeadLetteringOnFilterEvaluationExceptions(Boolean value) {
        this.deadLetteringOnFilterEvaluationExceptions = value;
    }

    /**
     * Gets the value of the defaultRuleDescription property.
     * 
     * @return
     *     possible object is
     *     {@link RuleDescription }
     *     
     */
    public RuleDescription getDefaultRuleDescription() {
        return defaultRuleDescription;
    }

    /**
     * Sets the value of the defaultRuleDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link RuleDescription }
     *     
     */
    public void setDefaultRuleDescription(RuleDescription value) {
        this.defaultRuleDescription = value;
    }

    /**
     * Gets the value of the messageCount property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMessageCount() {
        return messageCount;
    }

    /**
     * Sets the value of the messageCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMessageCount(Long value) {
        this.messageCount = value;
    }

    /**
     * Gets the value of the maxDeliveryCount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxDeliveryCount() {
        return maxDeliveryCount;
    }

    /**
     * Sets the value of the maxDeliveryCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxDeliveryCount(Integer value) {
        this.maxDeliveryCount = value;
    }

    /**
     * Gets the value of the enableBatchedOperations property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEnableBatchedOperations() {
        return enableBatchedOperations;
    }

    /**
     * Sets the value of the enableBatchedOperations property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEnableBatchedOperations(Boolean value) {
        this.enableBatchedOperations = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link EntityStatus }
     *     
     */
    public EntityStatus getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityStatus }
     *     
     */
    public void setStatus(EntityStatus value) {
        this.status = value;
    }

    /**
     * Gets the value of the forwardTo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForwardTo() {
        return forwardTo;
    }

    /**
     * Sets the value of the forwardTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForwardTo(String value) {
        this.forwardTo = value;
    }

    /**
     * Gets the value of the accessedAt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public DateTime getAccessedAt() {
        return accessedAt;
    }

    /**
     * Sets the value of the accessedAt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccessedAt(DateTime value) {
        this.accessedAt = value;
    }

    /**
     * Gets the value of the userMetadata property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserMetadata() {
        return userMetadata;
    }

    /**
     * Sets the value of the userMetadata property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserMetadata(String value) {
        this.userMetadata = value;
    }

    /**
     * Gets the value of the countDetails property.
     * 
     * @return
     *     possible object is
     *     {@link MessageCountDetails }
     *     
     */
    public MessageCountDetails getCountDetails() {
        return countDetails;
    }

    /**
     * Sets the value of the countDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageCountDetails }
     *     
     */
    public void setCountDetails(MessageCountDetails value) {
        this.countDetails = value;
    }

    /**
     * Gets the value of the autoDeleteOnIdle property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getAutoDeleteOnIdle() {
        return autoDeleteOnIdle;
    }

    /**
     * Sets the value of the autoDeleteOnIdle property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setAutoDeleteOnIdle(Duration value) {
        this.autoDeleteOnIdle = value;
    }

    /**
     * Gets the value of the entityAvailabilityStatus property.
     * 
     * @return
     *     possible object is
     *     {@link EntityAvailabilityStatus }
     *     
     */
    public EntityAvailabilityStatus getEntityAvailabilityStatus() {
        return entityAvailabilityStatus;
    }

    /**
     * Sets the value of the entityAvailabilityStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityAvailabilityStatus }
     *     
     */
    public void setEntityAvailabilityStatus(EntityAvailabilityStatus value) {
        this.entityAvailabilityStatus = value;
    }

}