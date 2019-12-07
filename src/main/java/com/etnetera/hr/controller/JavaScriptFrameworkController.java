package com.etnetera.hr.controller;

import com.etnetera.hr.data.JavaScriptFramework;
import com.etnetera.hr.data.Version;
import com.etnetera.hr.service.JavaScriptFrameworkService;
import com.etnetera.hr.service.VersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * Simple REST controller for accessing application logic.
 * 
 * @author Etnetera
 *
 */
@RequestMapping("/frameworks")
@RestController
public class JavaScriptFrameworkController extends EtnRestController {

	private final JavaScriptFrameworkService frameworkService;
	private final VersionService versionService;

	@Autowired
	public JavaScriptFrameworkController(JavaScriptFrameworkService frameworkService, VersionService versionService) {
		this.frameworkService = frameworkService;
		this.versionService = versionService;
	}

	@GetMapping()
	public Iterable<JavaScriptFramework> getFrameworks(@RequestParam(required = false) String name){
		if(name == null || name.isBlank()){
			return frameworkService.findAll();
		}
		return frameworkService.searchAllByName(name);
	}

	@PostMapping()
	@ResponseStatus(HttpStatus.CREATED)
	public JavaScriptFramework addFramework(@NotNull @Valid @RequestBody JavaScriptFramework framework){
		return frameworkService.save(framework);
	}

	@PutMapping("/{id}")
	public JavaScriptFramework updateFramework(@PathVariable Long id, @NotNull @Valid @RequestBody JavaScriptFramework jsFramework){
		if(!frameworkService.existsById(id)){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Framework with id "+ id + " not found");
		}

		return frameworkService.save(jsFramework);
	}

	@PostMapping("/{id}/addVersion")
	public JavaScriptFramework addVersion(@PathVariable Long id, @NotNull @Valid @RequestBody Version version){
		Optional<JavaScriptFramework> framework = frameworkService.findById(id);
		if(!framework.isPresent()){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Framework with id "+ id + " not found");
		}

		framework.get().addVersion(version);
		frameworkService.save(framework.get());
		return framework.get();
	}

	@DeleteMapping("/{id}/removeVersion")
	public JavaScriptFramework removeVersion(@PathVariable Long id, @NotNull @Valid @RequestBody Version version){
		Optional<JavaScriptFramework> framework = frameworkService.findById(id);
		if(!framework.isPresent()){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Framework with id "+ id + " not found");
		}

		if(!framework.get().getVersions().contains(version)){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Framework with id "+ id + " does not have version: "+version);
		}

		framework.get().removeVersion(version);
		frameworkService.save(framework.get());
		versionService.delete(version);
		return framework.get();
	}

	@DeleteMapping("/{id}")
	public void deleteFramework(@PathVariable Long id){
		if(!frameworkService.existsById(id)){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Framework with id "+ id + " not found");
		}
		frameworkService.deleteById(id);
	}


}
