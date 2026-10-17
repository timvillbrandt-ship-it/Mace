package com.maceclient.core;

public enum Category {
    MACE("Mace"), COMBAT("Combat"), VISUAL("Visual");
    public final String label;
    Category(String label) { this.label = label; }
}
