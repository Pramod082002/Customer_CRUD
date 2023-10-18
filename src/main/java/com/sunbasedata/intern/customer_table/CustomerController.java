package com.sunbasedata.intern.customer_table;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import jakarta.servlet.http.HttpSession;

@Controller
public class CustomerController {

    private String token;

    @GetMapping("/")
    public String viewHomePage(@ModelAttribute("LoginDetails") LoginDetails LoginDetails, Model model) {
        model.addAttribute("LoginDetails", LoginDetails);
        model.addAttribute("LoginFailed", false);
        return "login_page";
    }

    @PostMapping("/login_post")
    public String getToken(HttpSession session, @ModelAttribute("LoginDetails") LoginDetails LoginDetails, Model model){
        try{
            RestTemplate restTemplate = new RestTemplate();
            URI uri = new URI("https://qa2.sunbasedata.com/sunbase/portal/api/assignment_auth.jsp");
            String result = restTemplate.postForObject(uri, LoginDetails, String.class);
            ObjectMapper mapper = new JsonMapper();
            JsonNode json = mapper.readTree(result);
            token = "Bearer "+json.get("access_token").asText();
            session.setAttribute("Bearer_Token", token);
            System.out.println(token);
            return "redirect:/getCustomers";
        }catch(Exception e){
            model.addAttribute("LoginFailed", true);
            return "login_page";
        }
    }

    @GetMapping("/getCustomers")
    public String getCustomers(HttpSession session, @ModelAttribute("editCustomer") Customer customer,Model model) throws URISyntaxException{
        if(session.getAttribute("Bearer_Token")==null){
            return "access_denied";
        }
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI("https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=get_customer_list");
        HttpHeaders headers = new HttpHeaders();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();        
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));        
        messageConverters.add(converter);  
        restTemplate.setMessageConverters(messageConverters);
        headers.set("Authorization", session.getAttribute("Bearer_Token").toString());
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<Customer[]> result = restTemplate.exchange(uri, HttpMethod.GET, entity, Customer[].class);
        model.addAttribute("allCustomers", result.getBody());
        model.addAttribute("editCustomer",customer);
        return "get_customers";
    }

    @GetMapping("/add_customers")
    public String addCustomer(HttpSession session,@ModelAttribute("newCustomer") Customer customer, Model model){
        if(session.getAttribute("Bearer_Token")==null){
            return "access_denied";
        }
        model.addAttribute("newCustomer", customer);
        return "Add_customers";
    }

    @PostMapping("add_new_customers")
    public String addNewCustomer(HttpSession session,@ModelAttribute("newCustomer") Customer customer, Model model) throws URISyntaxException{
        if(session.getAttribute("Bearer_Token")==null){
            return "access_denied";
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", session.getAttribute("Bearer_Token").toString());
        HttpEntity<Customer> entity = new HttpEntity<>(customer, headers);
        URI uri = new URI("https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=create");
        String result = restTemplate.postForObject(uri, entity, String.class);
        System.out.println(result);
        return "redirect:/getCustomers";
    }

    @GetMapping("/edit/{uuid}")
    public String edit(HttpSession session, @PathVariable String uuid, @ModelAttribute("editCustomer") Customer customer,Model model){
        if(session.getAttribute("Bearer_Token")==null){
            return "access_denied";
        }
        model.addAttribute("editCustomer",customer);
        return "edit_page";
    }

    @PostMapping("/editCustomer/{uuid}")
    public String editCustomers(HttpSession session, @PathVariable String uuid, @ModelAttribute("newCustomer") Customer customer,Model model) throws URISyntaxException{
        if(session.getAttribute("Bearer_Token")==null){
            return "access_denied";
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", session.getAttribute("Bearer_Token").toString());
        HttpEntity<Customer> entity = new HttpEntity<>(customer,headers);
        URI uri = new URI("https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=update&uuid="+uuid);
        String result = restTemplate.postForObject(uri, entity, String.class);
        System.out.println(result);
        return "redirect:/getCustomers";
    }

    @GetMapping("/delete/{uuid}")
    public String deleteCustomers(HttpSession session, @PathVariable String uuid, @ModelAttribute("newCustomer") Customer customer) throws URISyntaxException{
        if(session.getAttribute("Bearer_Token")==null){
            return "access_denied";
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", session.getAttribute("Bearer_Token").toString());
        HttpEntity<Customer> entity = new HttpEntity<>(headers);
        URI uri = new URI("https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=delete&uuid="+uuid);
        String result = restTemplate.postForObject(uri, entity, String.class);
        System.out.println(result);
        return "redirect:/getCustomers";
    }

    @GetMapping("/log_out")
    public String logout(HttpSession session) {
    	System.out.println("Log Out");
        session.removeAttribute("Bearer_Token");
        return "redirect:/";
    }
    
    @GetMapping("/access_denied")
    public String accessDenied() {
        return "access_denied";
    }


}
