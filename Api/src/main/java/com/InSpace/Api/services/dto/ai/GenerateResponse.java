package com.InSpace.Api.services.dto.ai;

public class GenerateResponse {
    private String generation;
    private String error;
    private boolean generated;


    public GenerateResponse() {
    }

    public GenerateResponse(String generation) {
        this.generation = generation;
    }

    public String getGeneration() {
        return generation;
    }

    public void setGeneration(String generation) {
        this.generation = generation;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final GenerateResponse resp = new GenerateResponse();

        public Builder generation(String generation) {
            resp.setGeneration(generation);
            resp.setGenerated(true);
            return this;
        }

        public Builder error(String error) {
            resp.setError(error);
            resp.setGenerated(false);
            return this;
        }

        public GenerateResponse build() {
            return resp;
        }
    }
}
