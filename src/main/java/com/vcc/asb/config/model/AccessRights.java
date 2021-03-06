//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.01.17 at 12:06:23 PM CET 
//


package com.vcc.asb.config.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AccessRights.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AccessRights">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Manage"/>
 *     &lt;enumeration value="Send"/>
 *     &lt;enumeration value="Listen"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AccessRights")
@XmlEnum
public enum AccessRights {

    @XmlEnumValue("Manage")
    MANAGE("Manage"),
    @XmlEnumValue("Send")
    SEND("Send"),
    @XmlEnumValue("Listen")
    LISTEN("Listen");
    private final String value;

    AccessRights(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AccessRights fromValue(String v) {
        for (AccessRights c: AccessRights.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
