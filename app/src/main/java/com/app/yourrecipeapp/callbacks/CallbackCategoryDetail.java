package com.app.yourrecipeapp.callbacks;

import com.app.yourrecipeapp.models.Category;
import com.app.yourrecipeapp.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class CallbackCategoryDetail {

    public String status = "";
    public int count = -1;
    public int count_total = -1;
    public int pages = -1;
    public Category category = null;
    public List<Recipe> posts = new ArrayList<>();

}
