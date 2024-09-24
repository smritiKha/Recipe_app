package com.app.yourrecipeapp.callbacks;

import com.app.yourrecipeapp.models.Images;
import com.app.yourrecipeapp.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class CallbackRecipeDetail {

    public String status = "";
    public Recipe post = null;
    public List<Images> images = new ArrayList<>();
    public List<Recipe> related = new ArrayList<>();

}