package com.example.recipe.services;

import com.example.recipe.dao.RecipeRepository;
import com.example.recipe.entities.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public Optional<Recipe> findRecipeById(Long id) {
        return recipeRepository.findRecipeById(id);
    }

    public Recipe save(Recipe toSave) {
        return recipeRepository.save(toSave);
    }

    public void delete(long toDelete) {
        recipeRepository.deleteById(toDelete);
    }

    public boolean existsById(long id) {
        return recipeRepository.existsById(id);
    }

    public List<Recipe> findRecipesInCategory(String category){
        return recipeRepository.findAllByCategoryIgnoreCaseOrderByDateDesc(category);
    }

    public List<Recipe> findRecipesWithName(String name){
        return recipeRepository.findByNameContainingIgnoreCaseOrderByDateDesc(name);
    }

}
