package com.example.konditer.models;

public class IngredientGroup {
    private final String groupName;

    public IngredientGroup(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    @Override
    public String toString() {
        return groupName;
    }
}