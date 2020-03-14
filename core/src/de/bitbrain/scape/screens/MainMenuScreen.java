package de.bitbrain.scape.screens;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.bitbrain.braingdx.assets.SharedAssetManager;
import de.bitbrain.braingdx.context.GameContext2D;
import de.bitbrain.braingdx.graphics.pipeline.layers.RenderPipeIds;
import de.bitbrain.braingdx.graphics.postprocessing.AutoReloadPostProcessorEffect;
import de.bitbrain.braingdx.graphics.postprocessing.effects.Bloom;
import de.bitbrain.braingdx.graphics.postprocessing.effects.Vignette;
import de.bitbrain.braingdx.input.controller.NavigateableControllerInput;
import de.bitbrain.braingdx.input.keyboard.NavigateableKeyboardInput;
import de.bitbrain.braingdx.screen.BrainGdxScreen2D;
import de.bitbrain.braingdx.tweens.ActorTween;
import de.bitbrain.braingdx.tweens.GameCameraTween;
import de.bitbrain.braingdx.tweens.SharedTweenManager;
import de.bitbrain.braingdx.ui.NavigationMenu;
import de.bitbrain.braingdx.world.GameObject;
import de.bitbrain.scape.Colors;
import de.bitbrain.scape.ScapeGame;
import de.bitbrain.scape.assets.Assets;
import de.bitbrain.scape.progress.PlayerProgress;
import de.bitbrain.scape.ui.GlitchLabel;
import de.bitbrain.scape.ui.Styles;

import static de.bitbrain.scape.GameConfig.*;
import static de.bitbrain.scape.i18n.Bundle.get;
import static de.bitbrain.scape.i18n.Messages.*;

public class MainMenuScreen extends BrainGdxScreen2D<ScapeGame> {

   private NavigationMenu<TextButton> buttonMenu;

   public MainMenuScreen(ScapeGame game) {
      super(game);
   }

   private GameContext2D context;

   private boolean exiting = false;

   @Override
   protected void onCreate(GameContext2D context) {
      this.context = context;
      context.getScreenTransitions().in(1.5f);
      context.setBackgroundColor(Colors.BACKGROUND_VIOLET);
      setupUI(context);
      if (Gdx.app.getType() != Application.ApplicationType.Android && Gdx.app.getType() != Application.ApplicationType.iOS) {
         setupShaders();
      }
      setupInput(context);
   }

   @Override
   public void dispose() {
      super.dispose();
      Controllers.clearListeners();
   }

   @Override
   protected void onUpdate(float delta) {
      super.onUpdate(delta);
      if (!exiting && Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
         exiting = true;
         Gdx.app.exit();
      }
   }

   private void setupUI(final GameContext2D context) {

      boolean isMobile = Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS;

      Table layout = new Table();
      layout.setFillParent(true);

      Actor logo = createAnimatedLogo("scape");
      layout.add(logo).padBottom(80f).row();

      NavigationMenu.NavigationMenuStyle style = new NavigationMenu.NavigationMenuStyle();
      style.padding = isMobile ? MENU_BUTTON_PADDING_MOBILE : MENU_BUTTON_PADDING;
      style.hoverSound = SharedAssetManager.getInstance().get(Assets.Sounds.SELECT, Sound.class);
      style.enterSound = SharedAssetManager.getInstance().get(Assets.Sounds.SUBMIT, Sound.class);
      style.vertical = !isMobile;
      buttonMenu = new NavigationMenu<TextButton>(style);
      TextButton continueButton = new TextButton(get(MENU_MAIN_CONTINUE), Styles.BUTTON_MENU);
      GlitchLabel.GlitchLabelStyle glStyle = new GlitchLabel.GlitchLabelStyle();
      glStyle.fadeInDuration = 0.5f;
      glStyle.font =  Styles.BUTTON_MENU.font;
      glStyle.fontColor =  Styles.BUTTON_MENU.fontColor;
      final GlitchLabel label = new GlitchLabel(get(MENU_MAIN_CONTINUE), glStyle);
      label.setAlignment(Align.center);
      continueButton.setLabel(label);
      buttonMenu.add(continueButton, new ClickListener() {

         @Override
         public void clicked(InputEvent event, float x, float y) {
            context.getScreenTransitions().out(new StageSelectionScreen(getGame(), true), 0.5f);
            Tween.to(context.getGameCamera(), GameCameraTween.DEFAULT_ZOOM_FACTOR, 0.7f)
                  .target(0.001f)
                  .ease(TweenEquations.easeInExpo)
                  .start(SharedTweenManager.getInstance());
         }

         @Override
         public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
            super.enter(event, x, y, pointer, fromActor);
            label.glitchIn();
         }
      }).width(isMobile ? MENU_BUTTON_WIDTH_MOBILE : MENU_BUTTON_WIDTH)
        .height(isMobile ? MENU_BUTTON_HEIGHT_MOBILE : MENU_BUTTON_HEIGHT);
      buttonMenu.add(new TextButton(get(MENU_MAIN_NEWGAME), Styles.BUTTON_MENU), new ClickListener() {
               @Override
               public void clicked(InputEvent event, float x, float y) {
                  PlayerProgress progress = new PlayerProgress(null);
                  progress.reset();
                  context.getScreenTransitions().out(new IntroScreen(getGame()), 0.5f);
               }
      }).width(isMobile ? MENU_BUTTON_WIDTH_MOBILE : MENU_BUTTON_WIDTH)
        .height(isMobile ? MENU_BUTTON_HEIGHT_MOBILE : MENU_BUTTON_HEIGHT);
      buttonMenu.add(new TextButton(get(MENU_MAIN_EXIT), Styles.BUTTON_MENU), new ClickListener() {
               @Override
               public void clicked(InputEvent event, float x, float y) {
                  Gdx.app.exit();
               }
      }).width(isMobile ? MENU_BUTTON_WIDTH_MOBILE : MENU_BUTTON_WIDTH)
        .height(isMobile ? MENU_BUTTON_HEIGHT_MOBILE : MENU_BUTTON_HEIGHT);

      buttonMenu.next();

      layout.padTop(100f).add(buttonMenu).padBottom(80f).row();
      Label credits = new Label(get(MENU_MAIN_CREDITS) + "\n© 2020", Styles.LABEL_CREDITS);
      credits.setAlignment(Align.center);
      credits.getColor().a = 0.3f;
      layout.add(credits);
      context.getWorldStage().addActor(layout);
      context.getGameCamera().setStickToWorldBounds(false);
      context.getGameCamera().zoom(0.4f);
      context.getGameCamera().getInternalCamera().update();
   }

   @Override
   public Viewport getViewport(int width, int height, Camera camera) {
      return new ExtendViewport(width, height, camera);
   }

   private void setupInput(GameContext2D context) {
      context.getInputManager().register(new NavigateableControllerInput(buttonMenu));
      context.getInputManager().register(new NavigateableKeyboardInput(buttonMenu));
   }

   private void setupShaders() {
      AutoReloadPostProcessorEffect<Bloom> bloomEffect = context.getShaderManager().createBloomEffect();
      AutoReloadPostProcessorEffect<Vignette> vignetteEffect = context.getShaderManager().createVignetteEffect();
      bloomEffect.mutate(DEFAULT_BLOOM_CONFIG);
      context.getRenderPipeline().addEffects(RenderPipeIds.UI, vignetteEffect, bloomEffect);
   }

   private Actor createAnimatedLogo(String text) {
      HorizontalGroup logoGroup = new HorizontalGroup();
      for (int i = 0; i < text.length(); ++i) {
         Label character = new Label(text.charAt(i) + "", Styles.LABEL_LOGO);
         character.getColor().a = 0.3f;
         Tween.to(character, ActorTween.ALPHA, 1.9f)
               .delay(0.35f * i)
               .target(1f)
               .repeatYoyo(Tween.INFINITY, 0f)
               .ease(TweenEquations.easeInOutSine)
               .start(context.getTweenManager());
         logoGroup.addActor(character);
      }

      return logoGroup;
   }
}
