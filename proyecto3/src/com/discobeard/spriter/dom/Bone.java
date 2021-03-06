//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.01.18 at 06:33:53 PM MEZ 
//


package com.discobeard.spriter.dom;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Bone complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Bone">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="x" type="{http://www.w3.org/2001/XMLSchema}decimal" default="0" />
 *       &lt;attribute name="y" type="{http://www.w3.org/2001/XMLSchema}decimal" default="0" />
 *       &lt;attribute name="angle" type="{http://www.w3.org/2001/XMLSchema}decimal" default="0" />
 *       &lt;attribute name="scale_x" type="{http://www.w3.org/2001/XMLSchema}decimal" default="1.0" />
 *       &lt;attribute name="scale_y" type="{http://www.w3.org/2001/XMLSchema}decimal" default="1.0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Bone")
public class Bone {

    @XmlAttribute(name = "x")
    protected BigDecimal x;
    @XmlAttribute(name = "y")
    protected BigDecimal y;
    @XmlAttribute(name = "angle")
    protected BigDecimal angle;
    @XmlAttribute(name = "scale_x")
    protected BigDecimal scaleX;
    @XmlAttribute(name = "scale_y")
    protected BigDecimal scaleY;

    /**
     * Gets the value of the x property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getX() {
        if (x == null) {
            return new BigDecimal("0");
        } else {
            return x;
        }
    }

    /**
     * Sets the value of the x property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setX(BigDecimal value) {
        this.x = value;
    }

    /**
     * Gets the value of the y property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getY() {
        if (y == null) {
            return new BigDecimal("0");
        } else {
            return y;
        }
    }

    /**
     * Sets the value of the y property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setY(BigDecimal value) {
        this.y = value;
    }

    /**
     * Gets the value of the angle property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAngle() {
        if (angle == null) {
            return new BigDecimal("0");
        } else {
            return angle;
        }
    }

    /**
     * Sets the value of the angle property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAngle(BigDecimal value) {
        this.angle = value;
    }

    /**
     * Gets the value of the scaleX property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getScaleX() {
        if (scaleX == null) {
            return new BigDecimal("1.0");
        } else {
            return scaleX;
        }
    }

    /**
     * Sets the value of the scaleX property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setScaleX(BigDecimal value) {
        this.scaleX = value;
    }

    /**
     * Gets the value of the scaleY property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getScaleY() {
        if (scaleY == null) {
            return new BigDecimal("1.0");
        } else {
            return scaleY;
        }
    }

    /**
     * Sets the value of the scaleY property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setScaleY(BigDecimal value) {
        this.scaleY = value;
    }

}
