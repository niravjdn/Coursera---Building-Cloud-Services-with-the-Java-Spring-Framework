package org.magnum.mobilecloud.video;

import com.google.common.collect.Lists;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import javax.servlet.http.HttpServletResponse;
import org.magnum.mobilecloud.video.client.VideoNotFoundException;
import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/video")
public class VideoController {

  @Autowired
  private VideoRepository vRep;

  @RequestMapping(method = RequestMethod.GET)
  public @ResponseBody Collection<Video> getAllVideos() {
    return Lists.newArrayList(vRep.findAll());
  }

  @RequestMapping(method = RequestMethod.POST)
  public @ResponseBody Video addVideoMetadata(@RequestBody Video video) {
    return vRep.save(video);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public @ResponseBody Video getVideo(@PathVariable long id) {
    return getVideoById(id);
  }

  private Video getVideoById(long id) {
    Video video = vRep.findById(id);
    if (video == null) {
      throw new VideoNotFoundException();
    }
    return video;
  }

  @RequestMapping(value = "/{id}/like", method = RequestMethod.POST)
  public void likeVideo(@PathVariable long id, Principal p, HttpServletResponse response) {
    Video video = getVideoById(id);
    String username = p.getName();
    boolean liked = video.addLike(username);
    vRep.save(video);
    setResponseStatus(response, liked);
  }

  private void setResponseStatus(HttpServletResponse response, boolean b) {
    if (b) {
      response.setStatus(HttpServletResponse.SC_OK);
    } else {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/{id}/unlike",method = RequestMethod.POST)
  public void unlikeVideo(@PathVariable long id, Principal p, HttpServletResponse response) {
    Video video = getVideoById(id);
    String username = p.getName();
    boolean removed = video.removeLike(username);
    vRep.save(video);
    setResponseStatus(response, removed);
  }

  @RequestMapping(value = "search/findByName", method = RequestMethod.GET)
  public @ResponseBody Collection<Video> findVideoByName(@RequestParam("title") String title) {
    Collection<Video> videos = vRep.findByName(title);
    if (videos == null) {
      videos = Collections.emptyList();
    }
    return videos;
  }
  @RequestMapping(value = "search/findByDurationLessThan", method = RequestMethod.GET)
  public @ResponseBody Collection<Video> findVideoByDurationLessThan(@RequestParam("duration") long duration) {
    Collection<Video> videos = vRep.findByDurationLessThan(duration);
    if (videos == null) {
      videos = Collections.emptyList();
    }
    return videos;
  }
}
