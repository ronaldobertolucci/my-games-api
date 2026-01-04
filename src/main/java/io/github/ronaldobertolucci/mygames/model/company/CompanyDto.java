package io.github.ronaldobertolucci.mygames.model.company;

public record CompanyDto(
        Long id,
        String name
) {
    public CompanyDto(Company company) {
        this(company.getId(), company.getName());
    }
}
