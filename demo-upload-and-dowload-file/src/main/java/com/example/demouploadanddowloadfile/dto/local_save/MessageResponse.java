package com.example.demouploadanddowloadfile.dto.local_save;


import lombok.Data;

@Data
public class MessageResponse {
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }
}
