package com.campusfind.config;

import com.campusfind.entity.Category;
import com.campusfind.entity.Item;
import com.campusfind.entity.User;
import com.campusfind.enums.ItemStatus;
import com.campusfind.enums.ItemType;
import com.campusfind.enums.Role;
import com.campusfind.repository.CategoryRepository;
import com.campusfind.repository.ItemRepository;
import com.campusfind.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(CategoryRepository categoryRepository, UserRepository userRepository,
                      ItemRepository itemRepository, PasswordEncoder passwordEncoder) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedCategories();
        seedUsers();
        seedItems();
    }

    private void seedCategories() {
        if (categoryRepository.count() > 0) return;
        List<String> names = List.of("Mobile", "Laptop", "Wallet", "ID Card", "Books",
                "Documents", "Keys", "Bags", "Accessories", "Electronics", "Clothing", "Other");
        for (String name : names) {
            Category c = new Category();
            c.setName(name);
            categoryRepository.save(c);
        }
    }

    private void seedUsers() {
        if (userRepository.count() > 0) return;

        User admin = new User();
        admin.setFullName("System Admin");
        admin.setEmail("admin@abccollege.edu");
        admin.setCollegeId("ADMIN001");
        admin.setDepartment("Administration");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        admin.setEnabled(true);
        userRepository.save(admin);

        User staff = new User();
        staff.setFullName("Campus Staff");
        staff.setEmail("staff@abccollege.edu");
        staff.setCollegeId("STAFF001");
        staff.setDepartment("Security Office");
        staff.setPassword(passwordEncoder.encode("staff123"));
        staff.setRole(Role.STAFF);
        staff.setEnabled(true);
        userRepository.save(staff);

        User student1 = new User();
        student1.setFullName("Aarav Sharma");
        student1.setEmail("aarav.sharma@abccollege.edu");
        student1.setCollegeId("STU2026001");
        student1.setDepartment("Computer Engineering");
        student1.setYear("3rd Year");
        student1.setPassword(passwordEncoder.encode("student123"));
        student1.setRole(Role.STUDENT);
        student1.setEnabled(true);
        userRepository.save(student1);

        User student2 = new User();
        student2.setFullName("Priya Patil");
        student2.setEmail("priya.patil@abccollege.edu");
        student2.setCollegeId("STU2026002");
        student2.setDepartment("Electronics Engineering");
        student2.setYear("2nd Year");
        student2.setPassword(passwordEncoder.encode("student123"));
        student2.setRole(Role.STUDENT);
        student2.setEnabled(true);
        userRepository.save(student2);
    }

    private void seedItems() {
        if (itemRepository.count() > 0) return;

        User reporter = userRepository.findByEmail("aarav.sharma@abccollege.edu").orElse(null);
        User finder = userRepository.findByEmail("priya.patil@abccollege.edu").orElse(null);
        Category mobile = categoryRepository.findAll().stream()
                .filter(c -> c.getName().equals("Laptop")).findFirst().orElse(null);
        Category walletCat = categoryRepository.findAll().stream()
                .filter(c -> c.getName().equals("Wallet")).findFirst().orElse(null);

        if (reporter == null || finder == null) return;

        Item lostLaptop = new Item();
        lostLaptop.setUniqueItemCode("LF-2026-000101");
        lostLaptop.setTitle("Black HP Laptop");
        lostLaptop.setDescription("HP Pavilion, black color, has a college sticker on the back cover.");
        lostLaptop.setItemType(ItemType.LOST);
        lostLaptop.setCategory(mobile);
        lostLaptop.setLocation("Computer Lab 2");
        lostLaptop.setItemDate(LocalDate.now().minusDays(3));
        lostLaptop.setColor("Black");
        lostLaptop.setBrand("HP");
        lostLaptop.setIdentifyingFeatures("Scratch near the trackpad, college sticker on lid");
        lostLaptop.setStatus(ItemStatus.ACTIVE);
        lostLaptop.setUser(reporter);
        itemRepository.save(lostLaptop);

        Item foundWallet = new Item();
        foundWallet.setUniqueItemCode("LF-2026-000102");
        foundWallet.setTitle("Black Leather Wallet");
        foundWallet.setDescription("Found near the library entrance, contains a few cards.");
        foundWallet.setItemType(ItemType.FOUND);
        foundWallet.setCategory(walletCat);
        foundWallet.setLocation("Library Entrance");
        foundWallet.setItemDate(LocalDate.now().minusDays(1));
        foundWallet.setColor("Black");
        foundWallet.setBrand("Unknown");
        foundWallet.setStorageLocation("Security Office");
        foundWallet.setStatus(ItemStatus.ACTIVE);
        foundWallet.setUser(finder);
        itemRepository.save(foundWallet);
    }
}