package com.sengled.media.player.common;

import android.content.Context;
import android.graphics.Typeface;

import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.iconics.typeface.ITypeface;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by admin on 2017/5/11.
 */
public class CustomFont implements ITypeface{

    private static final String TTF_FILE = "icomoon.ttf";

    private static Typeface typeface = null;

    private static HashMap<String, Character> mChars;

    @Override
    public IIcon getIcon(String key) {
        return Icon.valueOf(key);
    }

    @Override
    public HashMap<String, Character> getCharacters() {
        if (mChars == null) {
            HashMap<String, Character> aChars = new HashMap<String, Character>();
            for (Icon v : Icon.values()) {
                aChars.put(v.name(),
                        v.character);
            }
            mChars = aChars;
        }

        return mChars;
    }

    @Override
    public String getMappingPrefix() {
        return "seg";
    }

    @Override
    public String getFontName() {
        return "sengled coustom font";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public int getIconCount() {
        return mChars.size();
    }

    @Override
    public Collection<String> getIcons() {
        Collection<String> icons = new LinkedList<String>();

        for (Icon value : Icon.values()) {
            icons.add(value.name());
        }

        return icons;
    }

    @Override
    public String getAuthor() {
        return "sengled";
    }

    @Override
    public String getUrl() {
        return "http://www.sengled.com";
    }

    @Override
    public String getDescription() {
        return "no";
    }

    @Override
    public String getLicense() {
        return "open source";
    }

    @Override
    public String getLicenseUrl() {
        return "no";
    }

    @Override
    public Typeface getTypeface(Context context) {
        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(),
                        "fonts/" + TTF_FILE);
            } catch (Exception e) {
                return null;
            }
        }
        return typeface;
    }

    public static enum Icon implements IIcon {
        seg_home1('\ue900'),
        seg_home2('\ue901'),
        seg_home3('\ue902'),
        seg_newspaper('\ue904'),
        seg_pencil('\ue905'),
        seg_office('\ue903'),
        seg_pencil2('\ue906'),
        seg_quill('\ue907'),
        seg_images('\ue90e'),
        seg_film('\ue913');

        char character;

        Icon(char character) {
            this.character = character;
        }

        public String getFormattedName() {
            return "{" + name() + "}";
        }

        public char getCharacter() {
            return character;
        }

        public String getName() {
            return name();
        }

        // remember the typeface so we can use it later
        private static ITypeface typeface;

        public ITypeface getTypeface() {
            if (typeface == null) {
                typeface = new CustomFont();
            }
            return typeface;
        }
    }
}
