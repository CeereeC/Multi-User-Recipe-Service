package com.example.recipe.controllers;

import com.example.recipe.entities.Recipe;
import com.example.recipe.entities.UserDetailsImpl;
import com.example.recipe.entities.idObject;
import com.example.recipe.services.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
public class RecipeController {
    @Autowired
    RecipeService recipeService;


    @PostMapping("/api/recipe/new")
    public idObject saveRecipe(
            @AuthenticationPrincipal UserDetailsImpl user,
            @Valid @RequestBody Recipe recipe) {

        Recipe recipeCreate = recipeService.save(new Recipe(
                recipe.getName(),
                recipe.getCategory(),
                recipe.getDescription(),
                recipe.getIngredients(),
                recipe.getDirections(),
                user.getUsername()
        ));

        return new idObject(recipeCreate.getId());
    }

    @GetMapping("/api/recipe/{id}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable long id) {
        Optional<Recipe> recipe = recipeService.findRecipeById(id);
        if (recipe.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(recipe.get(), HttpStatus.OK);
        }
    }

    @GetMapping("/api/recipe/search")
    public ResponseEntity<List<Recipe>> searchRecipe(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String name) {

        boolean categoryIsEmpty = category == null || category.equals("");
        boolean nameIsEmpty = name == null || name.equals("");

        if (categoryIsEmpty ^ nameIsEmpty) {
            return categoryIsEmpty
                    ? new ResponseEntity<>(recipeService.findRecipesWithName(name), HttpStatus.OK)
                    : new ResponseEntity<>(recipeService.findRecipesInCategory(category), HttpStatus.OK);

        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

    }

    @PutMapping("/api/recipe/{id}")
    public ResponseEntity<String> updateRecipe(
            @AuthenticationPrincipal UserDetailsImpl user,
            @Valid @RequestBody Recipe newRecipe,
            @PathVariable long id) {

        //Only edit recipe if user is owner of that recipe
        Optional<Recipe> oldRecipe = recipeService.findRecipeById(id);
        if (oldRecipe.isPresent()) {
            Recipe curRecipe = oldRecipe.get();
            if (curRecipe.getEmail().equals(user.getUsername())) {
                oldRecipe.map(recipe -> {
                    recipe.setName(newRecipe.getName());
                    recipe.setDescription(newRecipe.getDescription());
                    recipe.setCategory(newRecipe.getCategory());
                    recipe.setIngredients(newRecipe.getIngredients());
                    recipe.setDirections(newRecipe.getDirections());
                    return recipeService.save(recipe);
                });
                return ResponseEntity.noContent().build();
            } else throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("/api/recipe/{id}")
    public ResponseEntity<String> deleteRecipe(
            @AuthenticationPrincipal UserDetailsImpl user,
            @PathVariable long id) {

        Optional<Recipe> recipeToDelete = recipeService.findRecipeById(id);
        if (recipeToDelete.isPresent()) {
            Recipe curRecipe = recipeToDelete.get();
            if (curRecipe.getEmail().equals(user.getUsername())){
                recipeService.delete(id);
                return ResponseEntity.noContent().build();
            } else throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }


}
