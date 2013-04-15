package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.Sound;

import ro.thehunters.digi.recipeManager.Files;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;

public class FlagSound extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = new String[1];
        A[0] = "{flag} <arguments or false>";
        
        D = new String[8];
        D[0] = "Plays a sound at the workbench/furnace/crafter location for everyone or only for crafter if specified.";
        D[1] = null;
        D[2] = "Replace <arguments> with the following arguments separated by | character:";
        D[3] = "  play <sound>        = (Required) the sound name to play, sound names can be found in '" + Files.FILE_INFO_NAMES + "'.";
        D[4] = "  volume <0.0 to 1.0> = (Optional) sound's volume value";
        D[5] = "  pitch <0.0 to 4.0>  = (Optional) sound's pitch value";
        D[6] = "  player              = (Optional) use this to make the sound only play to the crafter";
        D[7] = "You can specify these arguments in any order.";
        
        E = new String[2];
        E[0] = "@sound play portal_travel | volume 0.7 | pitch 1.5";
        E[1] = "@sound volume 1.0 | play WOLF_HOWL";
    }
    
    // Flag code
    
    private boolean onlyPlayer = false;
    private Sound sound = null;
    private float volume = 1;
    private float pitch = 0;
    
    public FlagSound()
    {
        type = FlagType.SOUND;
    }
    
    public FlagSound(FlagSound flag)
    {
        this();
        
        onlyPlayer = flag.onlyPlayer;
        sound = flag.sound;
        volume = flag.volume;
        pitch = flag.pitch;
    }
    
    @Override
    public FlagSound clone()
    {
        return new FlagSound(this);
    }
    
    public boolean isOnlyPlayer()
    {
        return onlyPlayer;
    }
    
    public void setOnlyPlayer(boolean onlyPlayer)
    {
        this.onlyPlayer = onlyPlayer;
    }
    
    public Sound getSound()
    {
        return sound;
    }
    
    public void setSound(Sound sound)
    {
        this.sound = sound;
    }
    
    /**
     * @return volume from 0.0 to 1.0
     */
    public float getVolume()
    {
        return volume;
    }
    
    /**
     * @param volume
     *            from 0.0 to 1.0
     */
    public void setVolume(float volume)
    {
        if(volume < 0 || volume > 4)
        {
            RecipeErrorReporter.warning("Flag " + type + " has invalid 'volume' number range, must be between 0.0 and 1.0, trimmed.");
            this.volume = Math.min(Math.max(volume, 0.0f), 4.0f);
        }
        else
        {
            this.volume = volume;
        }
    }
    
    /**
     * @return pitch from 0.0 to 4.0
     */
    public float getPitch()
    {
        return pitch;
    }
    
    /**
     * @param pitch
     *            from 0.0 to 4.0
     */
    public void setPitch(float pitch)
    {
        if(pitch < 0 || pitch > 4)
        {
            RecipeErrorReporter.warning("Flag " + type + " has invalid 'pitch' number range, must be between 0.0 and 4.0, trimmed.");
            this.pitch = Math.min(Math.max(pitch, 0.0f), 4.0f);
        }
        else
        {
            this.pitch = pitch;
        }
    }
    
    @Override
    protected boolean onParse(String value)
    {
        String[] split = value.toLowerCase().split("\\|");
        
        for(String s : split)
        {
            s = s.trim();
            
            if(s.equals("player"))
            {
                onlyPlayer = true;
            }
            else if(s.startsWith("play"))
            {
                value = s.substring("play".length()).trim();
                
                if(value.isEmpty())
                {
                    RecipeErrorReporter.error("Flag " + type + " has 'play' argument with no sound!", "Read '" + Files.FILE_INFO_NAMES + "' for sounds list.");
                    return false;
                }
                
                try
                {
                    setSound(Sound.valueOf(value.toUpperCase()));
                }
                catch(Exception e)
                {
                    RecipeErrorReporter.error("Flag " + type + " has invalid 'play' argument value: " + value, "Read '" + Files.FILE_INFO_NAMES + "' for sounds list.");
                    return false;
                }
            }
            else if(s.startsWith("volume"))
            {
                value = s.substring("volume".length()).trim();
                
                if(value.isEmpty())
                {
                    RecipeErrorReporter.error("Flag " + type + " has 'volume' argument with number!", "Read '" + Files.FILE_INFO_FLAGS + "' for argument info.");
                    return false;
                }
                
                try
                {
                    setVolume(Float.valueOf(value));
                }
                catch(Exception e)
                {
                    RecipeErrorReporter.error("Flag " + type + " has invalid 'volume' argument float number: " + value, "Read '" + Files.FILE_INFO_FLAGS + "' for argument info.");
                    return false;
                }
            }
            else if(s.startsWith("pitch"))
            {
                value = s.substring("pitch".length()).trim();
                
                if(value.isEmpty())
                {
                    RecipeErrorReporter.error("Flag " + type + " has 'pitch' argument with number!", "Read '" + Files.FILE_INFO_FLAGS + "' for argument info.");
                    return false;
                }
                
                try
                {
                    setPitch(Float.valueOf(value));
                }
                catch(Exception e)
                {
                    RecipeErrorReporter.error("Flag " + type + " has invalid 'pitch' argument number: " + value, "Read '" + Files.FILE_INFO_FLAGS + "' for argument info.");
                    return false;
                }
            }
            else
            {
                RecipeErrorReporter.warning("Flag " + type + " has unknown argument: " + s, "Maybe it's spelled wrong, check it in " + Files.FILE_INFO_FLAGS + " file.");
            }
        }
        
        if(getSound() == null)
        {
            RecipeErrorReporter.error("Flag " + type + " doesn't have the 'play' argument!", "Read '" + Files.FILE_INFO_NAMES + "' for sounds list.");
            return false;
        }
        
        return true;
    }
    
    @Override
    protected void onCrafted(Args a)
    {
        if(onlyPlayer)
        {
            if(!a.hasPlayer())
            {
                a.addCustomReason("Needs player!");
                return;
            }
            
            a.player().playSound(a.hasLocation() ? a.location() : a.player().getLocation(), sound, volume, pitch);
        }
        else
        {
            if(!a.hasLocation())
            {
                a.addCustomReason("Needs location!");
                return;
            }
            
            a.location().getWorld().playSound(a.location(), sound, volume, pitch);
        }
    }
}