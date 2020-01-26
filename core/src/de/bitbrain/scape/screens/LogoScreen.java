package de.bitbrain.scape.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.bitbrain.braingdx.assets.SharedAssetManager;
import de.bitbrain.braingdx.context.GameContext2D;
import de.bitbrain.braingdx.graphics.animation.AnimationConfig;
import de.bitbrain.braingdx.graphics.animation.AnimationFrames;
import de.bitbrain.braingdx.graphics.animation.AnimationSpriteSheet;
import de.bitbrain.braingdx.graphics.pipeline.layers.RenderPipeIds;
import de.bitbrain.braingdx.graphics.postprocessing.AutoReloadPostProcessorEffect;
import de.bitbrain.braingdx.graphics.postprocessing.effects.Bloom;
import de.bitbrain.braingdx.graphics.postprocessing.effects.Vignette;
import de.bitbrain.braingdx.screen.BrainGdxScreen2D;
import de.bitbrain.braingdx.screens.AbstractScreen;
import de.bitbrain.braingdx.tweens.SharedTweenManager;
import de.bitbrain.braingdx.ui.AnimationDrawable;
import de.bitbrain.scape.Colors;
import de.bitbrain.scape.ScapeGame;
import de.bitbrain.scape.assets.Assets;
import de.bitbrain.scape.i18n.Bundle;
import de.bitbrain.scape.i18n.Messages;
import de.bitbrain.scape.input.logo.LogoControllerInputAdapter;
import de.bitbrain.scape.input.logo.LogoKeyboardInputAdapter;
import de.bitbrain.scape.input.logo.LogoMobileInputAdapter;
import de.bitbrain.scape.progress.PlayerProgress;
import de.bitbrain.scape.ui.GlitchLabel;
import de.bitbrain.scape.ui.Styles;

import static de.bitbrain.scape.GameConfig.DEFAULT_BLOOM_CONFIG;

public class LogoScreen extends BrainGdxScreen2D<ScapeGame> {

   private GameContext2D context;
   private PlayerProgress progress;

   private boolean exiting = false;
   private GlitchLabel slogan;

   public LogoScreen(ScapeGame game) {
      super(game);
   }

   @Override
   protected void onCreate(final GameContext2D context) {
      SharedAssetManager.getInstance().get(Assets.Musics.BITBRAIN, Music.class).play();
      SharedAssetManager.getInstance().get(Assets.Sounds.STARTUP, Sound.class).play(0.6f);
      SharedAssetManager.getInstance().get(Assets.Musics.COMPUTER_NOISE, Music.class).setLooping(true);
      SharedAssetManager.getInstance().get(Assets.Musics.COMPUTER_NOISE, Music.class).setVolume(0.2f);
      SharedAssetManager.getInstance().get(Assets.Musics.COMPUTER_NOISE, Music.class).play();
      this.context = context;
      this.progress = new PlayerProgress(null);
      context.getScreenTransitions().in(0.3f);
      context.setBackgroundColor(Colors.BACKGROUND_VIOLET);
      final Texture playerTexture = SharedAssetManager.getInstance().get(Assets.Textures.LOGO);
      AnimationSpriteSheet sheet = new AnimationSpriteSheet(playerTexture, 16);
      AnimationDrawable drawable = new AnimationDrawable(sheet,
            AnimationConfig.builder()
                  .registerFrames(AnimationDrawable.DEFAULT_FRAME_ID, AnimationFrames.builder()
                        .origin(0, 0)
                        .frames(16)
                        .direction(AnimationFrames.Direction.HORIZONTAL)
                        .playMode(Animation.PlayMode.NORMAL)
                        .duration(0.1f)
                  .build())
                  .build());

      Tween.call(new TweenCallback() {
         @Override
         public void onEvent(int i, BaseTween<?> baseTween) {
            if (!exiting) {
               AbstractScreen<?, ?> nextScreen = progress.isNewGame()
                     ? new IntroScreen(getGame())
                     : new MainMenuScreen(getGame());
               context.getScreenTransitions().out(nextScreen, 2.8f);
            }
         }
      }).delay(1.6f).start(SharedTweenManager.getInstance());

      Table layout = new Table();
      layout.setFillParent(true);

      Image image = new Image(drawable);
      layout.add(image).width(90f).height(90f).padBottom(40f).row();

      slogan = new GlitchLabel(Bundle.get(Messages.MENU_LOGO_CREDITS), Styles.LABEL_INTRO_BITBRAIN);
      slogan.setAlignment(Align.center);
      layout.add(slogan).width(Gdx.graphics.getWidth());

      context.getStage().addActor(layout);

      setupInput(context);
      if (Gdx.app.getType() != Application.ApplicationType.Android && Gdx.app.getType() != Application.ApplicationType.iOS) {
         setupShaders();
      }
   }

   public void exit() {
      if (!exiting) {
         AbstractScreen<?, ?> nextScreen = progress.isNewGame()
               ? new IntroScreen(getGame())
               : new MainMenuScreen(getGame());
         context.getScreenTransitions().out(nextScreen, 1f);
         exiting = true;
         SharedAssetManager.getInstance().get(Assets.Musics.BITBRAIN, Music.class).stop();
      }
   }

   @Override
   public void dispose() {
      super.dispose();
   }

   @Override
   public Viewport getViewport(int width, int height, Camera camera) {
      return new ExtendViewport(width, height, camera);
   }

   private void setupInput(GameContext2D context) {
      context.getInputManager().register(new LogoKeyboardInputAdapter(this));
      context.getInputManager().register(new GestureDetector(new LogoMobileInputAdapter(this)));
      context.getInputManager().register(new LogoControllerInputAdapter(this));
   }

   private void setupShaders() {
      AutoReloadPostProcessorEffect<Bloom> bloomEffect = context.getShaderManager().createBloomEffect();
      AutoReloadPostProcessorEffect<Vignette> vignetteEffect = context.getShaderManager().createVignetteEffect();
      bloomEffect.mutate(DEFAULT_BLOOM_CONFIG);
      context.getRenderPipeline().addEffects(RenderPipeIds.UI, vignetteEffect, bloomEffect);
   }
}
