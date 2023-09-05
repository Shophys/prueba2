package com.bolsadeideas.springboot.app.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.models.service.IClienteService;

import jakarta.validation.Valid;

@Controller
@SessionAttributes("cliente")
public class ClienteController {
	
	@Autowired
	private IClienteService clienteService;
	
	@GetMapping(value="/ver/{id}")
	public String ver(@PathVariable(value="id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		
		Cliente cliente = clienteService.findOne(id);
		if(cliente==null) {
			flash.addFlashAttribute("error", "El cliente no está en la base de datos");
			return "redirect:/listar";
		}
		
		model.put("cliente", cliente);
		model.put("titulo", "Detalle Cliente: " + cliente.getAutor());
		
		return "ver";
	}
	
	@RequestMapping(value = "/listar", method=RequestMethod.GET )
	public String listar(Model model) {
		model.addAttribute("titulo", "Listado de Libros");
		model.addAttribute("clientes", clienteService.findAll());
		return "listar";
	}
	
	@RequestMapping(value = "/form")
	public String crear(Map<String, Object> model) {
		
		Cliente cliente = new Cliente();
		model.put("cliente", cliente);
		model.put("titulo", "Información Libro-Autor");
		return "form";
	}
	
	@RequestMapping(value = "/form/{id}")
	public String editar(@PathVariable(value="id") Long id, Map<String, Object> model) {
	
	Cliente cliente = null;
	
	if(id > 0) {
		cliente = clienteService.findOne(id);		
	}
	else { 
		return "redirect:/listar";
	}
	model.put("cliente", cliente);
	model.put("titulo", "Editar Cliente");
	return "form";
	
	}
	
	@RequestMapping(value = "/form", method=RequestMethod.POST)
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model, @RequestParam("file") MultipartFile foto, SessionStatus status) {
		if(result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Cliente");
			return "form";
		}
		
		if (!foto.isEmpty()) {
			Path directorioRecursos = Paths.get("src//main.//resources//static//uploads");
			String rootPath = directorioRecursos.toFile().getAbsolutePath();
			try {
				byte [] bytes = foto.getBytes();
				Path rutaCompleta = Paths.get(rootPath + "//" + foto.getOriginalFilename());
				Files.write(rutaCompleta, bytes);
				cliente.setFoto(foto.getOriginalFilename());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		clienteService.save(cliente);
		status.setComplete();
		return "redirect:listar";
	}
	
	@RequestMapping(value = "/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id) {
		if(id > 0) {
			clienteService.delete(id);
		}
		return "redirect:/listar";
	}

}
