package org.magnum.mobilecloud.video.client;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "video not found")
public class VideoNotFoundException extends RuntimeException {
}