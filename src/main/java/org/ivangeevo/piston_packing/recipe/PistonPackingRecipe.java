package org.ivangeevo.piston_packing.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class PistonPackingRecipe implements Recipe<PackingRecipeInput> {
    protected final String group;
    protected final CraftingRecipeCategory category;
    final DefaultedList<Ingredient> ingredients;
    protected final Ingredient result;

    public PistonPackingRecipe(String group, CraftingRecipeCategory category, DefaultedList<Ingredient> ingredients, Ingredient result) {
        this.group = group;
        this.category = category;
        this.ingredients = ingredients;
        this.result = Ingredient.EMPTY;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(Blocks.MOVING_PISTON);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public boolean matches(PackingRecipeInput input, World world) {
        // Iterate over the ingredients in the recipe
        for (Ingredient ingredient : ingredients) {
            // Check if the input contains at least one matching ingredient
            boolean ingredientMatched = input.items().stream()
                    .anyMatch(ingredient); // Check if any item in the input matches the ingredient

            // If no matching ingredient is found in the input, return false
            if (!ingredientMatched) {
                return false;
            }
        }
        return true; // All ingredients match
    }



    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.result.getMatchingStacks()[0];
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public CraftingRecipeCategory getCategory() {
        return this.category;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return Recipe.super.isIgnoredInRecipeBook();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public ItemStack craft(PackingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return getResult(lookup);
    }

    public Ingredient getBlockResult() {
        return result;
    }

    public static class Type implements RecipeType<PistonPackingRecipe>
    {
        public static final Type INSTANCE = new Type();
        public static final String ID = "piston_packing";
    }

    public static class Serializer implements RecipeSerializer<PistonPackingRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "piston_packing";
        @Unique
        private static final Function<List<Ingredient>, DataResult<DefaultedList<Ingredient>>>
                INGREDIENTS_VALIDATOR = ingredients -> {
            Ingredient[] ingredientsArray = ingredients.stream().filter(ingredient -> !ingredient.isEmpty()).toArray(Ingredient[]::new);
            if (ingredientsArray.length == 0) {
                return DataResult.error(() -> "No ingredients for piston packing recipe");
            } else {
                return ingredientsArray.length > 64
                        ? DataResult.error(() -> "Too many ingredients for piston packing recipe")
                        : DataResult.success(DefaultedList.copyOf(Ingredient.EMPTY, ingredientsArray));
            }
        };

        protected static final MapCodec<PistonPackingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Codec.STRING.optionalFieldOf("group", "")
                                .forGetter(recipe -> recipe.group),
                        CraftingRecipeCategory.CODEC.fieldOf("category")
                                .orElse(CraftingRecipeCategory.MISC)
                                .forGetter(recipe -> recipe.category),
                        Ingredient.DISALLOW_EMPTY_CODEC
                                .listOf()
                                .fieldOf("ingredients")
                                .flatXmap(INGREDIENTS_VALIDATOR, DataResult::success)
                                .forGetter(recipe -> recipe.ingredients),
                        Ingredient.DISALLOW_EMPTY_CODEC
                                .fieldOf("result")
                                .forGetter(PistonPackingRecipe::getBlockResult)
                ).apply(instance, PistonPackingRecipe::new)
        );

        public static final PacketCodec<RegistryByteBuf, PistonPackingRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read
        );

        public Serializer() {
        }

        @Override
        public MapCodec<PistonPackingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, PistonPackingRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        public static PistonPackingRecipe read(RegistryByteBuf buf) {
            String group = buf.readString();
            CraftingRecipeCategory category = buf.readEnumConstant(CraftingRecipeCategory.class);
            int ingredientsSize = buf.readVarInt();
            DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(ingredientsSize, Ingredient.EMPTY);
            ingredients.replaceAll(ignored -> Ingredient.PACKET_CODEC.decode(buf));
            Ingredient result = Ingredient.PACKET_CODEC.decode(buf);
            return new PistonPackingRecipe(group, category, ingredients, result);
        }

        public static void write(RegistryByteBuf buf, PistonPackingRecipe recipe) {
            buf.writeString(recipe.group);
            buf.writeEnumConstant(recipe.category);
            buf.writeVarInt(recipe.ingredients.size());
            for (Ingredient ingredient : recipe.ingredients) {
                Ingredient.PACKET_CODEC.encode(buf, ingredient);
            }
            Ingredient.PACKET_CODEC.encode(buf, recipe.result);
        }
    }

    public static class JsonBuilder implements CraftingRecipeJsonBuilder {
        protected CraftingRecipeCategory category = CraftingRecipeCategory.MISC;
        protected DefaultedList<Ingredient> ingredients = DefaultedList.of();  // Initialize DefaultedList
        protected Ingredient result = Ingredient.EMPTY;
        protected final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();
        @Nullable
        protected String group;

        public static JsonBuilder create() {
            return new JsonBuilder();
        }

        public JsonBuilder category(CraftingRecipeCategory category) {
            this.category = category;
            return this;
        }

        public JsonBuilder ingredients(Ingredient... ingredients) {
            for (Ingredient ingredient : ingredients) {
                this.ingredient(ingredient);
            }
            return this;
        }

        public JsonBuilder ingredient(Ingredient ingredient) {
            this.ingredients.add(ingredient);  // Add to ingredients list
            return this;
        }

        public JsonBuilder ingredient(ItemStack itemStack) {
            this.criterion(RecipeProvider.hasItem(itemStack.getItem()), RecipeProvider.conditionsFromItem(itemStack.getItem()));
            return this.ingredient(Ingredient.ofStacks(itemStack));
        }

        public JsonBuilder ingredient(Item item, int count) {
            return this.ingredient(new ItemStack(item, count));
        }

        public JsonBuilder ingredient(Item item) {
            return this.ingredient(item, 1);
        }

        public JsonBuilder result(Block block) {
            if (block == null) {
                throw new IllegalStateException("Block result cannot be null");
            }
            this.result = Ingredient.ofStacks(block.asItem().getDefaultStack());
            return this;
        }

        @Override
        public JsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
            this.criteria.put(string, advancementCriterion);
            return this;
        }

        @Override
        public JsonBuilder group(@Nullable String string) {
            this.group = string;
            return this;
        }

        protected boolean isDefaultRecipe;
        public JsonBuilder markDefault() {
            this.isDefaultRecipe = true;
            return this;
        }

        @Override
        public Item getOutputItem() {
            return result.getMatchingStacks()[0].getItem();
        }

        @Override
        public void offerTo(RecipeExporter exporter) {
            this.offerTo(exporter,
                    RecipeProvider.getItemPath(getOutputItem())
                            + "_from_piston_packing_"
                            + RecipeProvider.getItemPath(this.ingredients.getFirst().getMatchingStacks()[0].getItem()));
        }

        public void offerTo(RecipeExporter exporter, Identifier recipeId) {
            this.validate(recipeId);
            // Ensure that ingredients and result are valid
            if (this.ingredients.isEmpty() || this.result.isEmpty()) {
                throw new IllegalStateException("Ingredients or result cannot be empty");
            }

            Advancement.Builder advancementBuilder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
            this.criteria.forEach(advancementBuilder::criterion);

            PistonPackingRecipe pistonPackingRecipe = new PistonPackingRecipe(
                    Objects.requireNonNullElse(this.group, ""),
                    this.category,
                    this.ingredients,
                    this.result
            );

            exporter.accept(recipeId, pistonPackingRecipe, advancementBuilder.build(recipeId.withPrefixedPath("recipes/" + this.category.asString() + "/")));
        }

        private void validate(Identifier recipeId) {
            if (this.criteria.isEmpty()) {
                throw new IllegalStateException("No way of obtaining recipe " + recipeId);
            }
        }
    }
}
