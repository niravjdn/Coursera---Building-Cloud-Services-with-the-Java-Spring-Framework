/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 */
package org.magnum.dataup;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.magnum.dataup.model.VideoStatus.VideoState;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class VideoController {

  public static final String VIDEO_SVC_PATH = "/video";
  public static final String VIDEO_DATA_PATH = VIDEO_SVC_PATH + "/{id}/data";
  private static final AtomicLong currentId = new AtomicLong(0L);

  // An in-memory list that the servlet uses to store the
  // videos that are sent to it by clients
  private VideoFileManager videoData;
  private Map<Long, Video> videos = new HashMap<Long, Video>();

  @RequestMapping(value = VIDEO_SVC_PATH, method = RequestMethod.GET)
  public @ResponseBody Collection<Video> getVideoList() {
    return videos.values();
  }

  @RequestMapping(value = VIDEO_DATA_PATH, method = RequestMethod.GET)
  public void getData(@PathVariable("id") long id, HttpServletResponse response)
      throws IOException {

    if (videoData == null)
      videoData = VideoFileManager.get();
    try {
      videoData.copyVideoData(videos.get(id), response.getOutputStream());
    } catch (Exception e) {
      throw new ResourceNotFoundException();
    }
  }

  @RequestMapping(value = VIDEO_SVC_PATH, method = RequestMethod.POST)
  public @ResponseBody Video addVideoMetadata(@RequestBody Video v, HttpServletRequest request)
      throws IOException {
    v.setId(currentId.incrementAndGet());
    v.setDataUrl(getUrlBaseForLocalServer(request) + "/" + VIDEO_SVC_PATH + v.getId() + "/data");
    videos.put(v.getId(), v);
    return v;
  }

  @RequestMapping(value = VIDEO_DATA_PATH, method = RequestMethod.POST)
  public @ResponseBody VideoStatus addVideoData(@PathVariable("id") long id,
      @RequestParam MultipartFile data) throws IOException {
    if (videoData == null)
      videoData = VideoFileManager.get();
    try {
      videoData.saveVideoData(videos.get(id), data.getInputStream());
    } catch (Exception e) {
      throw new ResourceNotFoundException();
    }
    return new VideoStatus(VideoState.READY);
  }

  private String getUrlBaseForLocalServer(HttpServletRequest request) {
    String base = "http://" + request.getServerName()
        + ((request.getServerPort() != 80) ? ":" + request.getServerPort() : "");
    return base;
  }

}
