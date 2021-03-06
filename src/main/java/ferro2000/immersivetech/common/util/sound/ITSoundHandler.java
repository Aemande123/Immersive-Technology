package ferro2000.immersivetech.common.util.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import java.util.HashMap;

public class ITSoundHandler extends PositionedSound implements ITickableSound {

    private static HashMap<BlockPos, ITSoundHandler> playingSounds = new HashMap<>();
    private BlockPos pos;

    public static void PlaySound(BlockPos posIn, SoundEvent soundIn, SoundCategory categoryIn, boolean repeatIn, float volumeIn, float pitchIn) {
        ITSoundHandler sound = playingSounds.get(posIn);
        if (sound == null) {
            sound = new ITSoundHandler(posIn, soundIn, categoryIn, repeatIn, volumeIn, pitchIn);
            playingSounds.put(posIn, sound);
        } else {
            sound.volume = volumeIn;
            sound.pitch = pitchIn;
            sound.repeat = repeatIn;
        }
    }

    public static void StopSound(BlockPos posIn) {
        ITSoundHandler sound = playingSounds.get(posIn);
        if (sound == null) return;
        sound.stopSound();
    }

    public ITSoundHandler(BlockPos posIn, SoundEvent soundIn, SoundCategory categoryIn, boolean repeatIn, float volumeIn, float pitchIn) {
        super(soundIn, categoryIn);
        this.pos = posIn;
        this.volume = volumeIn;
        this.pitch = pitchIn;
        this.xPosF = pos.getX() + 0.5f;
        this.yPosF = pos.getY() + 0.5f;
        this.zPosF = pos.getZ() + 0.5f;
        this.repeat = repeatIn;
        this.attenuationType = AttenuationType.NONE;
        Minecraft.getMinecraft().getSoundHandler().playSound(this);
    }

    @Override
    public boolean isDonePlaying() {
        return !playingSounds.containsValue(this);
    }

    @Override
    public void update() {}

    private void stopSound(boolean keepOnList) {
        if (!keepOnList) playingSounds.remove(pos);
        Minecraft.getMinecraft().getSoundHandler().stopSound(this);
    }

    private void stopSound() {
        stopSound(false);
    }

    public static void DeleteAllSounds() {
        playingSounds.forEach((blockPos, itSoundHandler) -> itSoundHandler.stopSound(true));
        playingSounds.clear();
    }
}