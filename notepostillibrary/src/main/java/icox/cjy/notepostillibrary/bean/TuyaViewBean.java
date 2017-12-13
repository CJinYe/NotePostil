package icox.cjy.notepostillibrary.bean;

import java.io.File;
import java.util.List;

import icox.cjy.notepostillibrary.constants.DrawPath;


/**
 * @author Administrator
 * @version $Rev$
 * @time 2017-5-6 18:14
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class TuyaViewBean {
    public List<DrawPath> savePaths;
    public int currentColor;
    public int backgroundColor;
    public int currentSize;
    public int currentStyle;
    public String text;
    public File bitmapPath;
    public boolean isChangerBackground;

    //保存时的屏幕宽高
    public int saveScreenWidth;
    public int saveScreenHeight;

    public int BACKGROUND_TYPE = 0;
    public final int BACKGROUND_NORMAL = 1;
    public final int BACKGROUND_LINE = 2;
}

