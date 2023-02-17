package com.nals.auction.api.v1;

import com.nals.auction.bloc.CompanyCrudBloc;
import com.nals.auction.dto.CompanyDto;
import com.nals.utils.controller.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.Validator;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyCrudController
    extends BaseController {

    private final CompanyCrudBloc companyCrudBloc;

    public CompanyCrudController(final Validator validator, final CompanyCrudBloc companyCrudBloc) {
        super(validator);
        this.companyCrudBloc = companyCrudBloc;
    }

    @PostMapping
    public ResponseEntity<?> createCompany(@Valid @RequestBody final CompanyDto dto) {
        return created(companyCrudBloc.createCompany(dto));
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getCompanyDetail() {
        return ok(companyCrudBloc.getCompanyDetail());
    }

    @PutMapping
    public ResponseEntity<?> updateCompany(@Valid @RequestBody final CompanyDto dto) {
        companyCrudBloc.updateCompany(dto);
        return noContent();
    }
}
