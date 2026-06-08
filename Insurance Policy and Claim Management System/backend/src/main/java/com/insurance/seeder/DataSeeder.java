package com.insurance.seeder;

import com.insurance.entity.Customer;
import com.insurance.entity.InsuranceProduct;
import com.insurance.entity.PolicyPlan;
import com.insurance.entity.User;
import com.insurance.entity.enums.PremiumType;
import com.insurance.entity.enums.ProductType;
import com.insurance.entity.enums.Role;
import com.insurance.repository.CustomerRepository;
import com.insurance.repository.InsuranceProductRepository;
import com.insurance.repository.PolicyPlanRepository;
import com.insurance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final InsuranceProductRepository productRepository;
    private final PolicyPlanRepository planRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            log.info("Database already seeded. Skipping seeder...");
            return;
        }

        log.info("Seeding initial data...");

        // 1. Seed Admin
        User admin = User.builder()
                .fullName("System Administrator")
                .email("admin@insurance.com")
                .password(passwordEncoder.encode("Admin@123"))
                .mobileNumber("9876543210")
                .role(Role.ADMIN)
                .active(true)
                .build();
        userRepository.save(admin);
        log.info("Seeded Admin: admin@insurance.com");

        // 2. Seed Agent
        User agent = User.builder()
                .fullName("Insurance Agent")
                .email("agent@insurance.com")
                .password(passwordEncoder.encode("Agent@123"))
                .mobileNumber("9876543211")
                .role(Role.AGENT)
                .active(true)
                .build();
        userRepository.save(agent);
        log.info("Seeded Agent: agent@insurance.com");

        // 3. Seed Customer User and profile
        User customerUser = User.builder()
                .fullName("John Doe")
                .email("customer@insurance.com")
                .password(passwordEncoder.encode("Customer@123"))
                .mobileNumber("9876543212")
                .role(Role.CUSTOMER)
                .active(true)
                .build();
        customerUser = userRepository.save(customerUser);

        Customer customerProfile = Customer.builder()
                .user(customerUser)
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .address("123 Main Street")
                .city("Chicago")
                .state("Illinois")
                .pinCode("60601")
                .nomineeName("Jane Doe")
                .nomineeRelation("Spouse")
                .build();
        customerRepository.save(customerProfile);
        log.info("Seeded Customer: customer@insurance.com");

        // 4. Seed Products
        InsuranceProduct healthProd = createProduct("Health Insurance", ProductType.HEALTH, "Comprehensive health and medical insurance plans");
        InsuranceProduct motorProd = createProduct("Motor Insurance", ProductType.MOTOR, "Vehicle coverages for cars, motorcycles, and commercial vehicles");
        InsuranceProduct lifeProd = createProduct("Life Insurance", ProductType.LIFE, "Term life and savings-linked insurance coverages");
        InsuranceProduct travelProd = createProduct("Travel Insurance", ProductType.TRAVEL, "International and domestic travel insurance plans");

        // 5. Seed Plans
        // Health Plans
        createPlan(healthProd, "Family Health Care", new BigDecimal("500000.00"), new BigDecimal("12000.00"), PremiumType.ANNUAL, 1, "Covers hospitalization, day care treatments, and pre/post hospital expenses for family.");
        createPlan(healthProd, "Senior Citizen Medical Cover", new BigDecimal("300000.00"), new BigDecimal("15000.00"), PremiumType.ANNUAL, 1, "Special medical coverage for age 60+ individuals with pre-existing disease support.");

        // Motor Plans
        createPlan(motorProd, "Comprehensive Car Insurance", new BigDecimal("1000000.00"), new BigDecimal("8000.00"), PremiumType.ANNUAL, 1, "Comprehensive coverage against own damages, third-party liability, theft and natural disasters.");
        createPlan(motorProd, "Two Wheeler Protection", new BigDecimal("100000.00"), new BigDecimal("1500.00"), PremiumType.ANNUAL, 1, "Essential shield for motorbikes against accidents, theft and public liabilities.");

        // Life Plans
        createPlan(lifeProd, "Term Life Standard", new BigDecimal("5000000.00"), new BigDecimal("10000.00"), PremiumType.ANNUAL, 10, "High coverage pure term life insurance with affordable rates.");
        createPlan(lifeProd, "Whole Life Shield", new BigDecimal("2000000.00"), new BigDecimal("25000.00"), PremiumType.ANNUAL, 20, "Life insurance policy extending up to 99 years of age with cash value accumulation.");

        // Travel Plans
        createPlan(travelProd, "Globe Trotter Plus", new BigDecimal("750000.00"), new BigDecimal("2000.00"), PremiumType.ONE_TIME, 1, "Medical emergencies, passport loss, flight cancellations and baggage delay cover for worldwide trips.");

        log.info("Data seeding completed successfully!");
    }

    private InsuranceProduct createProduct(String name, ProductType type, String desc) {
        InsuranceProduct prod = InsuranceProduct.builder()
                .productName(name)
                .productType(type)
                .description(desc)
                .active(true)
                .build();
        return productRepository.save(prod);
    }

    private void createPlan(InsuranceProduct product, String name, BigDecimal coverage, BigDecimal premium, PremiumType type, int duration, String terms) {
        PolicyPlan plan = PolicyPlan.builder()
                .insuranceProduct(product)
                .planName(name)
                .coverageAmount(coverage)
                .premiumAmount(premium)
                .premiumType(type)
                .duration(duration)
                .termsAndConditions(terms)
                .active(true)
                .build();
        planRepository.save(plan);
    }
}
