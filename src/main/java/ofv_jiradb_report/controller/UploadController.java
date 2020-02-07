package ofv_jiradb_report.controller;

import ofv_jiradb_report.MainApp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Calendar;
import java.util.Date;

@Controller
public class UploadController {
    @Value("${registry.build}")
    private String registry_build;

    public long ts;
    public String fileName;

    @GetMapping("/")
    public String index() {
        return "main";
    }

    public String isNewDataExists() {
        return registry_build;
    }

    @PostMapping("/uploadStatus/{F_project_name}")
    public String permission_static(@RequestParam("F_start_date") String F_start_date,
                                    @RequestParam("F_end_date") String F_end_date,
                                    @PathVariable("F_project_name") String F_project_name,
                                    @RequestParam(name = "F_assignee_to_users", required = false) String[] F_assignee_to_users,
                                    RedirectAttributes redirectAttributes) throws Exception {
        try {
            System.out.println("start uploadStatus");
            if (F_assignee_to_users == null)
                F_assignee_to_users = new String[]{""};
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("F_project_name", F_project_name);
            modelAndView.addObject("F_start_date", F_start_date);
            modelAndView.addObject("F_end_date", F_end_date);
            modelAndView.addObject("F_assignee_to_users", F_assignee_to_users);
            ts = getTimestamp();
            fileName = getName(modelAndView);
            System.out.println("fileName= " + fileName);
            MainApp mainApp = new MainApp();
            mainApp.main(F_project_name, F_start_date, F_end_date, F_assignee_to_users, ts);
            redirectAttributes.addFlashAttribute("message", "Done '");
            redirectAttributes.addFlashAttribute("F_start_date", F_start_date);
            redirectAttributes.addFlashAttribute("F_end_date", F_end_date);
            redirectAttributes.addFlashAttribute("F_assignee_to_users", F_assignee_to_users);
        } catch (Exception x) {
            System.out.println(x);
        }
        return "redirect:/downloads";
    }

    @RequestMapping(method = {RequestMethod.GET}, value = "/downloads")
    public ResponseEntity<InputStreamResource> downloadCSV(HttpServletResponse response) {
        try {
            String tsfileName = MainApp.path + ts + ".xls";
            File theXls = new File(tsfileName);
            HttpHeaders respHeaders = new HttpHeaders();
            MediaType mediaType = new MediaType("text", "xls");
            respHeaders.setContentType(mediaType);
            respHeaders.setContentDispositionFormData("attachment", fileName + ".xls");
            InputStreamResource isr = new InputStreamResource(new FileInputStream(theXls));
            return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
        } catch (Exception e) {
            String messagge = "Error in CSV creation; " + e.getMessage();
            System.out.println(messagge);
            return new ResponseEntity<InputStreamResource>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public long getTimestamp() {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        System.out.println("Calender - Time in milliseconds : " + calendar.getTimeInMillis());
        long ts = calendar.getTimeInMillis();
        return ts;
    }

    public String getName(ModelAndView modelAndView) {
        return modelAndView.getModel().get("F_project_name").toString()
                .concat(modelAndView.getModel().get("F_start_date").toString())
                .concat(modelAndView.getModel().get("F_end_date").toString());
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        System.out.println("return page");
        return "uploadStatus";
    }
}