//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.01.26 at 11:17:35 AM CET 
//


package com.vcc.asb.metrics.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for QueueMetrics complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="QueueMetrics">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schemas.microsoft.com/windowsazure}BaseMetrics">
 *       &lt;sequence>
 *         &lt;element name="QueueName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Incoming" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="Outgoing" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="Length" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="Size" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QueueMetrics", propOrder = {
    "queueName",
    "incoming",
    "outgoing",
    "length",
    "size"
})
@XmlRootElement(name = "QueueMetrics")
public class QueueMetrics
    extends BaseMetrics
{

    @XmlElement(name = "QueueName")
    protected String queueName;
    @XmlElement(name = "Incoming")
    protected Integer incoming;
    @XmlElement(name = "Outgoing")
    protected Integer outgoing;
    @XmlElement(name = "Length")
    protected Integer length;
    @XmlElement(name = "Size")
    protected Integer size;

    /**
     * Gets the value of the queueName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQueueName() {
        return queueName;
    }

    /**
     * Sets the value of the queueName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQueueName(String value) {
        this.queueName = value;
    }

    /**
     * Gets the value of the incoming property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIncoming() {
        return incoming;
    }

    /**
     * Sets the value of the incoming property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIncoming(Integer value) {
        this.incoming = value;
    }

    /**
     * Gets the value of the outgoing property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOutgoing() {
        return outgoing;
    }

    /**
     * Sets the value of the outgoing property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOutgoing(Integer value) {
        this.outgoing = value;
    }

    /**
     * Gets the value of the length property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLength() {
        return length;
    }

    /**
     * Sets the value of the length property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLength(Integer value) {
        this.length = value;
    }

    /**
     * Gets the value of the size property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSize(Integer value) {
        this.size = value;
    }

}