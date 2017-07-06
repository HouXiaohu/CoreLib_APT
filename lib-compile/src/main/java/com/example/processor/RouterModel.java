package com.example.processor;

import javax.lang.model.element.TypeElement;

/**
 * Created by baixiaokang on 16/12/30.
 */

public class RouterModel {
    TypeElement element;//当前的Activity
    String actionName;//当前Activity的ActionName
    public TypeElement getElement() {
        return element;
    }

    public void setElement(TypeElement mElement) {
        this.element = mElement;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String mActionName) {
        this.actionName = mActionName;
    }

}
