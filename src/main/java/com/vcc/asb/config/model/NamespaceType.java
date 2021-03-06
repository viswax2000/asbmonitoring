//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.01.20 at 06:25:51 PM CET 
//


package com.vcc.asb.config.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NamespaceType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="NamespaceType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Messaging"/>
 *     &lt;enumeration value="Mixed"/>
 *     &lt;enumeration value="NotificationHub"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "NamespaceType")
@XmlEnum
public enum NamespaceType {

    @XmlEnumValue("Messaging")
    MESSAGING("Messaging"),
    @XmlEnumValue("Mixed")
    MIXED("Mixed"),
    @XmlEnumValue("NotificationHub")
    NOTIFICATION_HUB("NotificationHub");
    private final String value;

    NamespaceType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static NamespaceType fromValue(String v) {
        for (NamespaceType c: NamespaceType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
