package com.aiyaapp.aiyamediaeditor.vedio;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/30 0030.
 * 视频合并
 */

public class VideoMerge {


    private String workingPath;//输出文件目录
    private ArrayList<String> videosToMerge;//需要合并的视频的路径集合
    private String outName = "record.mp4";//输出的视频名称
    private String outSoudName = "record.aac";//输出的音频名称

    public VideoMerge(String workingPath) {
        this.workingPath = workingPath;
        init();
    }


    private void init() {
        File dirFile = new File(workingPath);
        if (!dirFile.exists()) return;
        File[] files = dirFile.listFiles();
        videosToMerge = new ArrayList<>();
        for (File f : files) {
            if (f.getName().endsWith(".mp4")) {
                videosToMerge.add(f.getAbsolutePath());
            }
        }
    }

    /**
     * 开始合成
     */
    public boolean merge() {
        boolean isOk = false;
        int count = videosToMerge.size();
        if(count == 0){
            return  isOk;
        }
        try {
            Movie[] inMovies = new Movie[count];
            for (int i = 0; i < count; i++) {
                inMovies[i] = MovieCreator.build(videosToMerge.get(i));
            }

            List<Track> videoTracks = new LinkedList<>();
            List<Track> audioTracks = new LinkedList<>();

            //提取所有视频和音频的通道
            for (Movie m : inMovies) {
                for (Track t : m.getTracks()) {
                    if (t.getHandler().equals("soun")) {
                        audioTracks.add(t);
                    }
                    if (t.getHandler().equals("vide")) {
                        videoTracks.add(t);
                    }
                    if (t.getHandler().equals("")) {

                    }
                }
            }

//            if (audioTracks.size() > 0) {
//                result.addTrack(new AppendTrack(audioTracks
//                        .toArray(new Track[audioTracks.size()])));
//            }


            //生成音频文件
            Movie sounResult = new Movie();
            if (audioTracks.size() > 0) {
                sounResult.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
            }
            Container out = new DefaultMp4Builder().build(sounResult);
            try {
                FileChannel fc = new RandomAccessFile(workingPath + outSoudName, "rw").getChannel();
                out.writeContainer(fc);
                fc.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //添加通道到新的视频里
            Movie result = new Movie();
            //生成视频文件
            if (videoTracks.size() > 0) {
                result.addTrack(new AppendTrack(videoTracks
                        .toArray(new Track[videoTracks.size()])));
            }
            Container mp4file = new DefaultMp4Builder()
                    .build(result);
            //开始生产mp4文件
            File storagePath = new File(workingPath);
            FileOutputStream fos = new FileOutputStream(new File(storagePath, outName));
            FileChannel fco = fos.getChannel();
            mp4file.writeContainer(fco);
            fco.close();
            fos.close();
            isOk = true;
            return isOk;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isOk;
    }


}
