package com.example.app;

public class VideoBuilder {
    private String audioFile;
    private String videoFile;
    private String outputFile;

    public VideoBuilder(String audioFile, String videoFile){
        this.audioFile = audioFile;
        this.videoFile = videoFile;
    }

    public String getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }

    public String getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(String videoFile) {
        this.videoFile = videoFile;
    }

    public void generateVideo(){

        String[] command = new String[]{"ffmpeg", "-i", audioFile, "-i", videoFile, "-acodec", "copy", "-vcodec", "copy", "-shortest", "output.mp4"};
        ProcessBuilder pb = new ProcessBuilder(command);

        try {
            Process process = pb.start();
            process.waitFor();
        } catch (Exception e) {
            System.out.println(e);
        }
    }   
}