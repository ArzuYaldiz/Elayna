package com.example.mycloset.dataClasses;


public class TaskStatusResponse {
    private String status;
    private ModelUrls model_urls;

    public String getStatus() { return status; }
    public ModelUrls getModel_urls() { return model_urls; }

    public class ModelUrls {
        private String glb;

        public String getGlb() { return glb; }
    }
}
