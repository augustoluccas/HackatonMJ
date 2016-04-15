package br.org.projeto.vigilante.business.singletons;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import br.org.projeto.vigilante.business.database.DatabaseManager;
import br.org.projeto.vigilante.business.vo.ReportVO;
import br.org.projeto.vigilante.rest.VigilanteAPI;
import br.org.projeto.vigilante.util.ConnectionUtil;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

@EBean(scope = EBean.Scope.Singleton)
public class SyncHelper {

    private final String CONTENT_TYPE = "multipart/form-data;boundary=--ABC123Boundary";

    @RootContext
    protected Context mContext;

    @Bean
    protected DatabaseManager mDatabaseManager;

    private Queue<ReportVO> mQueue;

    public void save() {
        mDatabaseManager.startDatabase();
        List<ReportVO> reports = mDatabaseManager.getReports();
        mQueue = new LinkedList<>();
        if (reports != null && !reports.isEmpty()) {
            mQueue.addAll(reports);
            consumeQueue();
        }
    }

    private void consumeQueue() {
        final ReportVO poll = mQueue.poll();
        if (poll != null) {
            if (poll.isOnlyWifi()) {
                if (ConnectionUtil.isOnWifi(mContext)) {
                    makeRequest(poll);
                } else {
                    consumeQueue();
                }
            } else {
                makeRequest(poll);
            }
        }
    }

    private void makeRequest(final ReportVO vo) {

        VigilanteAPI.getServices(mContext)
                .sendReport(vo.toJson(mContext), getAttachFile(vo), CONTENT_TYPE, new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        deleteRecord(vo);
                        consumeQueue();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        consumeQueue();
                    }
                });

    }

    private TypedFile getAttachFile(final ReportVO vo) {

        TypedFile file = null;
        if (!TextUtils.isEmpty(vo.getImagePath())) {
            file = new TypedFile("application/octet-stream", new File(vo.getImagePath()));
        } else if (!TextUtils.isEmpty(vo.getAudioPath())) {
            file = new TypedFile("application/octet-stream", new File(vo.getAudioPath()));
        } else if (vo.getVideoPath() != null) {
            file = new TypedFile("application/octet-stream", new File(vo.getVideoPath()));
        }

        return file;
    }


    private void deleteRecord(final ReportVO vo) {
        mDatabaseManager.deleteReport(vo);
    }
}
