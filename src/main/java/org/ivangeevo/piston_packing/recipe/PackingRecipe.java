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
import net.minecraft.registry.tag.TagKey;
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

public class PackingRecipe implements Recipe<PackingRecipeInput> {
    protected final String group;
    protected final CraftingRecipeCategory category;
    final DefaultedList<IngredientWithCount> ingredients;
    protected final Ingredient result;

    public PackingRecipe(String group, CraftingRecipeCategory category, DefaultedList<IngredientWithCount> ingredients, Ingredient result) {
        this.group = group;
        this.category = category;
        this.ingredients = DefaultedList.copyOf(IngredientWithCount.EMPTY, ingredients.toArray(new IngredientWithCount[0]));;
        this.result = result;
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
        for (IngredientWithCount ingredient : ingredients) {
            // Calculate the total count of items in the input that match the ingredient
            int totalMatchingCount = input.items().stream()
                    .filter(itemStack -> ingredient.toVanilla().test(itemStack))
                    .mapToInt(ItemStack::getCount)
                    .sum();

            // If the total matching count is less than the required count for the ingredient, return false
            if (totalMatchingCount < ingredient.count()) {
                return false;
            }
        }
        return true; // All ingredients match with sufficient counts
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
        DefaultedList<Ingredient> defaultedList = DefaultedList.of();
        defaultedList.addAll(this.ingredients.stream().map(IngredientWithCount::toVanilla).toList());
        return defaultedList;
    }

    public DefaultedList<IngredientWithCount> getIngredientsWithCount() {
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

    public static class Type implements RecipeType<PackingRecipe>
    {
        public static final Type INSTANCE = new Type();
        public static final String ID = "packing";
    }

    public static class Serializer implements RecipeSerializer<PackingRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "packing";
        @Unique
        private static final Function<List<IngredientWithCount>, DataResult<DefaultedList<IngredientWithCount>>>
                INGREDIENTS_VALIDATOR = ingredients -> {
            IngredientWithCount[] ingredientsArray = ingredients.stream().filter(ingredient -> !ingredient.ingredient().isEmpty()).toArray(IngredientWithCount[]::new);
            if (ingredientsArray.length == 0) {
                return DataResult.error(() -> "No ingredients for piston packing recipe");
            } else {
                return ingredientsArray.length > 64
                        ? DataResult.error(() -> "Too many ingredients for piston packing recipe")
                        : DataResult.success(DefaultedList.copyOf(IngredientWithCount.EMPTY, ingredientsArray));
            }
        };

        protected static final MapCodec<PackingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Codec.STRING.optionalFieldOf("group", "")
                                .forGetter(recipe -> recipe.group),
                        CraftingRecipeCategory.CODEC.fieldOf("category")
                                .orElse(CraftingRecipeCategory.MISC)
                                .forGetter(recipe -> recipe.category),
                        IngredientWithCount.Serializer.DISALLOW_EMPTY_CODEC.codec()
                                .listOf()
                                .fieldOf("ingredients")
                                .flatXmap(INGREDIENTS_VALIDATOR, DataResult::success)
                                .forGetter(recipe -> recipe.ingredients),
                        Ingredient.DISALLOW_EMPTY_CODEC
                                .fieldOf("result")
                                .forGetter(PackingRecipe::getBlockResult)
                ).apply(instance, PackingRecipe::new)
        );

        public static final PacketCodec<RegistryByteBuf, PackingRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read
        );

        public Serializer() {
        }

        @Override
        public MapCodec<PackingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, PackingRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        public static PackingRecipe read(RegistryByteBuf buf) {
            String group = buf.readString();
            CraftingRecipeCategory category = buf.readEnumConstant(CraftingRecipeCategory.class);
            int ingredientsSize = buf.readVarInt();
            DefaultedList<IngredientWithCount> ingredients = DefaultedList.ofSize(ingredientsSize, IngredientWithCount.EMPTY);
            ingredients.replaceAll(ignored -> IngredientWithCount.Serializer.PACKET_CODEC.decode(buf));
            Ingredient result = Ingredient.PACKET_CODEC.decode(buf);
            return new PackingRecipe(group, category, ingredients, result);
        }

        public static void write(RegistryByteBuf buf, PackingRecipe recipe) {
            buf.writeString(recipe.group);
            buf.writeEnumConstant(recipe.category);
            buf.writeVarInt(recipe.ingredients.size());
            for (IngredientWithCount ingredient : recipe.ingredients) {
                IngredientWithCount.Serializer.PACKET_CODEC.encode(buf, ingredient);
            }
            Ingredient.PACKET_CODEC.encode(buf, recipe.result);
        }
    }

    public static class JsonBuilder implements CraftingRecipeJsonBuilder {
        protected CraftingRecipeCategory category = CraftingRecipeCategory.MISC;
        protected DefaultedList<IngredientWithCount> ingredients = DefaultedList.of();  // Initialize DefaultedList
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

        public JsonBuilder ingredients(IngredientWithCount... ingredients) {
            for (IngredientWithCount ingredient : ingredients) {
                this.ingredient(ingredient);
            }
            return this;
        }

        public JsonBuilder ingredient(IngredientWithCount ingredient) {
            this.ingredients.add(ingredient);
            return this;
        }

        public JsonBuilder ingredient(ItemStack itemStack) {
            this.criterion(RecipeProvider.hasItem(itemStack.getItem()), RecipeProvider.conditionsFromItem(itemStack.getItem()));
            return this.ingredient(IngredientWithCount.fromStack(itemStack));
        }

        public JsonBuilder ingredient(Item item, int count) {
            return this.ingredient(new ItemStack(item, count));
        }

        public JsonBuilder ingredient(Item item) {
            return this.ingredient(item, 1);
        }

        public JsonBuilder ingredient(TagKey<Item> itemTag, int count) {
            this.criterion("has_" + itemTag.id().getPath(), RecipeProvider.conditionsFromTag(itemTag));
            return this.ingredient(IngredientWithCount.fromTag(itemTag, count));
        }

        public JsonBuilder ingredient(TagKey<Item> itemTag) {
            return this.ingredient(itemTag, 1);
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
                            + RecipeProvider.getItemPath(this.ingredients.getFirst().ingredient().getMatchingStacks()[0].getItem()));
        }

        public void offerTo(RecipeExporter exporter, Identifier recipeId) {
            this.validate(recipeId);
            // Ensure that ingredients and result are valid
            if (this.ingredients.isEmpty() || this.result.isEmpty()) {
                throw new IllegalStateException("Ingredients or result cannot be empty");
            }

            Advancement.Builder advancementBuilder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
            this.criteria.forEach(advancementBuilder::criterion);

            PackingRecipe pistonPackingRecipe = new PackingRecipe(
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
