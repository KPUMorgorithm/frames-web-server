package org.morgorithm.frames.controller;

import org.morgorithm.frames.store.RegisterURLStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class URLMappingController {
    @RequestMapping(value = "/u/{url}", method = {RequestMethod.GET, RequestMethod.POST})
    public String urlMapping(@PathVariable String url) {
        String source = RegisterURLStore.getInstance().getSourceURL(url);
        if (source == null) {
            return "redirect:/error";
        }
        return "redirect:"+source;
    }
}
