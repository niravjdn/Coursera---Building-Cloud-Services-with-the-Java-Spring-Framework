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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.common.net.MediaType;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.magnum.dataup.model.*;
import org.magnum.dataup.model.VideoStatus.VideoState;

@Controller
public class VideoController {

  /**
   * You will need to create one or more Spring controllers to fulfill the requirements of the
   * assignment. If you use this file, please rename it to something other than "AnEmptyController"
   * 
   * 
   * ________ ________ ________ ________ ___ ___ ___ ________ ___ __ |\ ____\|\ __ \|\ __ \|\ ___ \
   * |\ \ |\ \|\ \|\ ____\|\ \|\ \ \ \ \___|\ \ \|\ \ \ \|\ \ \ \_|\ \ \ \ \ \ \ \\\ \ \ \___|\ \ \/
   * /|_ \ \ \ __\ \ \\\ \ \ \\\ \ \ \ \\ \ \ \ \ \ \ \\\ \ \ \ \ \ ___ \ \ \ \|\ \ \ \\\ \ \ \\\ \
   * \ \_\\ \ \ \ \____\ \ \\\ \ \ \____\ \ \\ \ \ \ \_______\ \_______\ \_______\ \_______\ \
   * \_______\ \_______\ \_______\ \__\\ \__\ \|_______|\|_______|\|_______|\|_______|
   * \|_______|\|_______|\|_______|\|__| \|__|
   * 
   * 
   */

  private Map<Long, Video> videos = new HashMap<Long, Video>();


  @RequestMapping(method = RequestMethod.GET, value = "/video")
  public @ResponseBody Collection<Video> getVideoList() {
    Collection<Video> videoList = new ArrayList<Video>(videos.values());
    return videoList;
  }

  @RequestMapping(method = RequestMethod.POST, value = "/video")
  public @ResponseBody Video addVideo(@RequestBody Video v) {
    AtomicLong id = new AtomicLong(1L);
    long videoID = id.longValue();
    Video video = Video.create().withContentType("video/mpeg").withDuration(v.getDuration())
        .withSubject(v.getSubject()).withTitle(v.getTitle()).build();
    video.setId(videoID);

    video.setDataUrl("a");

    videos.put(videoID, video);
    return v;
  }

  @RequestMapping(method = RequestMethod.POST, value = "/video/{id}/data")
  public VideoStatus setVideoData(long id, TypedFile videoData) throws IOException {
    VideoFileManager fm = VideoFileManager.get();
    fm.saveVideoData(videos.get(id), videoData.in());
    VideoStatus status = new VideoStatus(VideoState.READY);
    return status;
  }

  @RequestMapping(method = RequestMethod.GET, value = "/video/{id}/data")
  public Response getData(long id) {
    VideoFileManager fm = VideoFileManager.get();
    Response r = videos.get(id).getDataUrl().in();
    return null;
  }
}
