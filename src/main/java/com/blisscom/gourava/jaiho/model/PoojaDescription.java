package com.blisscom.gourava.jaiho.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by gourava on 12/26/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PoojaDescription implements Serializable {

    String title;
    String procedure;
    String importance;
    String image_name;
    float cost;
    String averageDuration;
    float priestPreparationCharge;
    String prepDetails;

    public PoojaDescription() {
    }

    public PoojaDescription(String title, String procedure, String importance, String image_name, float cost, String averageDuration, float priestPreparationCharge, String prepDetails) {
        this.title = title;
        this.procedure = procedure;
        this.importance = importance;
        this.image_name = image_name;
        this.cost = cost;
        this.averageDuration = averageDuration;
        this.priestPreparationCharge = priestPreparationCharge;
        this.prepDetails = prepDetails;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getAverageDuration() {
        return averageDuration;
    }

    public void setAverageDuration(String averageDuration) {
        this.averageDuration = averageDuration;
    }

    public float getPriestPreparationCharge() {
        return priestPreparationCharge;
    }

    public void setPriestPreparationCharge(float priestPreparationCharge) {
        this.priestPreparationCharge = priestPreparationCharge;
    }

    public String getPrepDetails() {
        return prepDetails;
    }

    public void setPrepDetails(String prepDetails) {
        this.prepDetails = prepDetails;
    }
}
