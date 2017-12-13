package icox.cjy.notepostil;

import android.app.Application;

import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.OkHttpNetworkExecutor;

/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-11-23 17:17
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class MyApplocation extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NoHttp.initialize(this, new NoHttp.Config()
                .setNetworkExecutor(new OkHttpNetworkExecutor())
        );

        Logger.setDebug(true);
        Logger.setTag("NOHTTP_TEST");
    }
}
