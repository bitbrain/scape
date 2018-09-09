package de.bitbrain.scape.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import de.bitbrain.braingdx.assets.SharedAssetManager;
import de.bitbrain.braingdx.graphics.BitmapFontBaker;
import de.bitbrain.scape.Colors;
import de.bitbrain.scape.assets.Assets;

public final class Styles {

   private static final AssetManager m = SharedAssetManager.getInstance();

   public static final Label.LabelStyle LABEL_INGAME_POINTS = new Label.LabelStyle();
   public static final Label.LabelStyle LABEL_INGAME_CAPTION = new Label.LabelStyle();
   public static final Label.LabelStyle LABEL_INGAME_DESCRIPTION = new Label.LabelStyle();
   public static final Label.LabelStyle LABEL_INGAME_INTERACTION = new Label.LabelStyle();
   public static final Label.LabelStyle LABEL_SELECTION_CAPTION = new Label.LabelStyle();
   public static final Label.LabelStyle LABEL_INTRO_COMMAND = new Label.LabelStyle();

   public static void init() {
      LABEL_INGAME_POINTS.font = BitmapFontBaker.bake(Assets.Fonts.UI_NUMBER, Gdx.graphics.getHeight() / 8);
      LABEL_INGAME_POINTS.fontColor = Colors.PRIMARY_RED;

      LABEL_INGAME_CAPTION.font = BitmapFontBaker.bake(Assets.Fonts.UI_NUMBER, Gdx.graphics.getHeight() / 5);
      LABEL_INGAME_CAPTION.fontColor = Colors.PRIMARY_RED;

      LABEL_INGAME_DESCRIPTION.font = BitmapFontBaker.bake(Assets.Fonts.UI_NUMBER, Gdx.graphics.getHeight() / 10);
      LABEL_INGAME_DESCRIPTION.fontColor = Colors.PRIMARY_BLUE;

      LABEL_INGAME_INTERACTION.font = BitmapFontBaker.bake(Assets.Fonts.UI_NUMBER, Gdx.graphics.getHeight() / 20);
      LABEL_INGAME_INTERACTION.fontColor = Colors.PRIMARY_BLUE;

      LABEL_INTRO_COMMAND.font = BitmapFontBaker.bake(Assets.Fonts.UI_NUMBER, Gdx.graphics.getHeight() / 24);
      LABEL_INTRO_COMMAND.fontColor = Colors.PRIMARY_BLUE;

      LABEL_SELECTION_CAPTION.font = BitmapFontBaker.bake(Assets.Fonts.UI_NUMBER, Gdx.graphics.getHeight() / 10);
      LABEL_SELECTION_CAPTION.fontColor = Colors.PRIMARY_RED;
   }
}