package com.aiyaapp.aiyamediaeditor.vedio;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/30 0030.
 * 视频的分割
 */

public class VideoClip {
    private String filePath;//视频路径
    private String workingPath;//输出路径
    private String outName;//输出文件名
    private double startTime;//剪切起始时间
    private double endTime;//剪切结束时间

    private void clip() {
        try {
            Movie movie = MovieCreator.build(filePath);

            List<Track> tracks = movie.getTracks();
            movie.setTracks(new LinkedList<Track>());
            //移除旧的通道
            boolean timeCorrected = false;

            //计算剪切时间
            for (Track track : tracks) {
                if (track.getSyncSamples() != null
                        && track.getSyncSamples().length > 0) {
                    if (timeCorrected) {
                        throw new RuntimeException(
                                "The startTime has already been corrected by another track with SyncSample. Not Supported.");
                    }
                    startTime = VideoHelper.correctTimeToSyncSample(track, startTime, false);
                    endTime = VideoHelper.correctTimeToSyncSample(track, endTime, true);
                    timeCorrected = true;
                }
            }

            for (Track track : tracks) {
                long currentSample = 0;
                double currentTime = 0;
                double lastTime = 0;
                long startSample1 = -1;
                long endSample1 = -1;

                for (int i = 0; i < track.getSampleDurations().length; i++) {
                    long delta = track.getSampleDurations()[i];
                    if (currentTime > lastTime && currentTime <= startTime) {
                        startSample1 = currentSample;
                    }
                    if (currentTime > lastTime && currentTime <= endTime) {
                        endSample1 = currentSample;
                    }
                    lastTime = currentTime;
                    currentTime += (double) delta
                            / (double) track.getTrackMetaData().getTimescale();
                    currentSample++;
                }
                movie.addTrack(new CroppedTrack(track, startSample1, endSample1));// new
            }

            //合成视频mp4
            Container out = new DefaultMp4Builder().build(movie);
            File storagePath = new File(workingPath);
            storagePath.mkdirs();
            FileOutputStream fos = new FileOutputStream(new File(storagePath, outName));
            FileChannel fco = fos.getChannel();
            out.writeContainer(fco);
            fco.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}