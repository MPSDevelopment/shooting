package tech.shooting.ipsc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.ipsc.pojo.Categories;
import tech.shooting.ipsc.service.CategoryService;

import java.util.List;

@Controller
@RequestMapping(value = ControllerAPI.CATEGORY_CONTROLLER)
@Api(value = ControllerAPI.CATEGORY_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    @GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.CATEGORY_CONTROLLER_GET_ALL_CATEGORIES)
    @ApiOperation(value = "Get list categories")
    public ResponseEntity<List<Categories>> getCategories() {
        return new ResponseEntity<>(categoryService.getCategories(), HttpStatus.OK);
    }

    @GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.CATEGORY_CONTROLLER_GET_CATEGORY_BY_ID)
    @ApiOperation(value = "Get category by id")
    public ResponseEntity<Categories> getCategoryById(@PathVariable(value = ControllerAPI.PATH_VARIABLE_CATEGORY_ID)Long categoryId) throws BadRequestException {
        return new ResponseEntity<>(categoryService.getCategoryById(categoryId), HttpStatus.OK);
    }
}
