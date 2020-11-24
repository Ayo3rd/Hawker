package itp341.otegbade.opeoluwa.myfinal.project.app.model;

import android.content.Context;

import java.util.*;

public class MenuSingleton {

    private static MenuSingleton singleton;

    private ArrayList<MenuItem> allMenuItems;
    private Context context;

    private MenuSingleton(Context context){
        this.context = context;
        allMenuItems = new ArrayList<>();
    }

    //Get instance of singleton
    public static MenuSingleton get(Context context){
        if(singleton == null){
            singleton = new MenuSingleton(context);
        }
        return singleton;
    }

    public ArrayList<MenuItem> getAllMenuItems(){
        return allMenuItems;
    }

    public void setAllMenuItems(java.util.ArrayList<MenuItem> allMenuItems) {
        this.allMenuItems = allMenuItems;
    }

    public MenuItem getMenuItem(int i){
        return allMenuItems.get(i);
    }

    public void addMenuItem(MenuItem myMenuItem){
        allMenuItems.add(myMenuItem);
    }

    public void updateMenuItem(int i, MenuItem mItem){
        allMenuItems.set(i, mItem);
    }

    public void removeMenuItem(int i){
        allMenuItems.remove(i);
    }

}
