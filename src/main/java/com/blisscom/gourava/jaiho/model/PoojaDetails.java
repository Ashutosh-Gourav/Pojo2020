package com.blisscom.gourava.jaiho.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by gourava on 12/26/16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class PoojaDetails implements Serializable {
    private String name;
    private int id;
    private String imageName;
    private String procedure;
    private String importance;
    private String poojaItems;
    private int cost;
    private int prepCost;


    public PoojaDetails() {
    }

    public PoojaDetails(String name, int id, String imageName, String procedure, String importance, String poojaItems, int cost, int prepCost) {
        this.name = name;
        this.id = id;
        this.imageName = imageName;
        this.procedure = procedure;
        this.importance = importance;
        this.poojaItems = poojaItems;
        this.cost = cost;
        this.prepCost = prepCost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
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

    public String getPoojaItems() {
        return poojaItems;
    }

    public void setPoojaItems(String poojaItems) {
        this.poojaItems = poojaItems;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getPrepCost() {
        return prepCost;
    }

    public void setPrepCost(int prepCost) {
        this.prepCost = prepCost;
    }
}
