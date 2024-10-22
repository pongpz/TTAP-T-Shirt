//package com.project.ttaptshirt.controller.adminController;
//
//
//import com.project.ttaptshirt.entity.DiaChi;
//import com.project.ttaptshirt.entity.User;
//import com.project.ttaptshirt.service.ChucVuService;
////import com.project.ttaptshirt.service.UserService;
//import com.project.ttaptshirt.service.UsersService;
//import com.project.ttaptshirt.service.impl.UserServiceImpl;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//@Controller
//@RequestMapping("/TTAP/User/")
//public class UserController {
//
//    @Autowired
//    private UsersService serUser;
//
//    @Autowired
//    private ChucVuService serCv;
//
//
//
//    @GetMapping("home")
//    public String home(Model mol){
//        List<User> userList = serUser.findAll();
//        mol.addAttribute("nvLst",userList);
//        return"/user/index";
//    }
//
//    @GetMapping("employee")
//    public String employee(Model mol){
//        List<User> employeeList = serUser.findByCv("employee");
//        mol.addAttribute("empoLst",employeeList);
//        return "/user/nhanvien/index";
//    }
//
//    @GetMapping("employee/find")
//    public String findUser(@RequestParam(value = "name", required = false) String name, Model mol){
//        List<User> users;
//        if (name == null || name.isEmpty()) {
//            users = serUser.findByCv("employee"); // Assuming you have a method to retrieve all users
//        } else {
//            users = serUser.findByUsers(name);
//        }
//        mol.addAttribute("empoLst",users);
//        return "/user/nhanvien/index";
//    }
//
//    @GetMapping("customer/find")
//    public String findCos(@RequestParam(value = "name", required = false) String name, Model mol){
//        List<User> users;
//        if (name == null || name.isEmpty()) {
//            users = serUser.findByCv("customer"); // ko tim dc la back ve ban dau
//        } else {
//            users = serUser.findByUsers(name);// neu bị trùng tên là vẫn hiện lên table
//        }
//        mol.addAttribute("cusLst",users);
//        return "/user/khachhang/index";
//    }
//
//    @GetMapping("customer")
//    public String customer(Model mol){
//        List<User> customerList = serUser.findByCv("customer");
//        mol.addAttribute("cusLst",customerList);
//        return "/user/khachhang/index";
//    }
//
//    @GetMapping("newNv")
//    public String addNv(Model mol){
//        mol.addAttribute("user", new User());
//        mol.addAttribute("cv",serCv.findAll());
//        return "/user/nhanvien/dangky";
//    }
//    @GetMapping("newKh")
//    public String addKh(Model mol){
//        mol.addAttribute("user", new User());
//        mol.addAttribute("cv",serCv.findAll());
//        return "/user/khachhang/dangky";
//    }
//    @GetMapping("new")
//    public String add(Model mol){
//        mol.addAttribute("user", new User());
//        mol.addAttribute("cv",serCv.findAll());
//        return "/user/dangky";
//    }
//
//    @PostMapping("save")
//    public String createUser(@Valid @ModelAttribute User user, BindingResult result,Model mol){
//        if (result.hasErrors()){
//            mol.addAttribute("cv",serCv.findAll());
//            return "/user/dangky";
//        }
//        user.setNgayTao(LocalDate.now());
//        user.setNgaySua(LocalDate.now());
//        serUser.save(user);
//        return "redirect:home";
//    }
//
//    @GetMapping("delete/{id}")
//    public String deleteUser(@PathVariable("id") Long id,Model mol){
//        Optional<User> user = serUser.findById(id);
////                .orElseThrow(() -> new IllegalArgumentException("ID ko ton tai:" + id));
//        serUser.deleteById(id);
//        return "redirect:/TTAP/User/employee";
//    }
//    @GetMapping("deletekh/{id}")
//    public String deleteKh(@PathVariable("id") Long id,Model mol){
//        Optional<User> user = serUser.findById(id);
////                .orElseThrow(() -> new IllegalArgumentException("ID ko ton tai:" + id));
//        serUser.deleteById(id);
//        return "redirect:/TTAP/User/customer";
//    }
//    @GetMapping("detail/{id}")
//    public String showDetail(@PathVariable("id") Long id,Model mol){
//        Optional<User> user = serUser.findById(id);
////                .orElseThrow(() -> new IllegalArgumentException("ID ko ton tai:"+ id));
//        mol.addAttribute("user",user);
//        mol.addAttribute("cv",serCv.findAll());
//        mol.addAttribute("diaChi",user.get().getDc() != null ? user.get().getDc() : new DiaChi());
//        return "/user/nhanvien/update";
//    }
//
//    @GetMapping("detailKh/{id}")
//    public String showDetailKh(@PathVariable("id") Long id,Model mol){
//        Optional<User> user = serUser.findById(id);
////                .orElseThrow(() -> new IllegalArgumentException("ID ko ton tai:"+ id));
//        mol.addAttribute("user",user);
//        mol.addAttribute("cv",serCv.findAll());
//        mol.addAttribute("diaChi",user.get().getDc() != null ? user.get().getDc() : new DiaChi());
//        return "/user/khachhang/update";
//    }
//
//    @PostMapping("update")
//    public String updateUser(@Valid User user,BindingResult result, Model mol){
//        if (result.hasErrors()){
//            user.setId(user.getId());
//            return "/user/update";
//        }
//        Optional<User> existingUser = serUser.findById(user.getId());
////                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + user.getId()));
//        user.setNgayTao(existingUser.get().getNgayTao());
//        user.setNgaySua(LocalDate.now());
//        serUser.save(user);
//        return "redirect:/TTAP/User/employee";
//    }
//
//    @PostMapping("updateKh")
//    public String updateKh(@Valid User user,BindingResult result, Model mol){
//        if (result.hasErrors()){
//            user.setId(user.getId());
//            return "/user/update";
//        }
//        Optional<User> existingUser = serUser.findById(user.getId());
////                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + user.getId()));
//        user.setNgayTao(existingUser.get().getNgayTao());
//        user.setNgaySua(LocalDate.now());
//        serUser.save(user);
//        return "redirect:/TTAP/User/employee";
//    }
//
//    @PostMapping("/{id}/DiaChi")
//    public String getupdateDiachi(@PathVariable("id") Long id, @ModelAttribute DiaChi diaChi,Model mol){
//        serUser.updateDiachi(id,diaChi);
//        return "redirect:/TTAP/User/detail/"+ id;
//    }
//
//    @PostMapping("/{id}/DiaChiKh")
//    public String getupdateDiachiKh(@PathVariable("id") Long id, @ModelAttribute DiaChi diaChi,Model mol){
//        serUser.updateDiachi(id,diaChi);
//        return "redirect:/TTAP/User/detailKh/"+ id;
//    }
//
////    @PostMapping("register")
////    public String registerUser(@Valid @ModelAttribute User user,BindingResult result,Model mol) {
////        if (result.hasErrors()){
////            mol.addAttribute("cv",serCv.findAll());
////            return "/user/dangky";
////        }
////        user.setNgayTao(LocalDate.now());
////        user.setNgaySua(LocalDate.now());
////        serUser.createUser(user);
////        mol.addAttribute("message", "Chi tiết tài khoản đã đc gửi.");
////        return "redirect:/TTAP/User/home";
////    }
//
////    @PostMapping(value = "create")
////    public ResponseEntity<String> create(
////            @Valid @ModelAttribute User user, BindingResult result, Model mol
////    ) {
////        if (result.hasErrors()) {
////            mol.addAttribute("cv", serCv.findAll());
////            return ResponseEntity.badRequest().body("Validation errors occurred");
////        }
////        user.setNgayTao(LocalDate.now());
////        user.setNgaySua(LocalDate.now());
////        serUser.registerUser(user);
////        return ResponseEntity.ok().body();
////    }
//
//    @PostMapping("/create")
//    public String registerUser(
//            @Valid @ModelAttribute("user") User user, BindingResult result,
//            Model model, RedirectAttributes redirectAttributes
//    ) {
//        if (result.hasErrors()) {
//            model.addAttribute("cvList",serCv.findAll());
//            return "user/register"; // Trả về lại form nếu có lỗi
//        }
//
//        user.setNgayTao(LocalDate.now());
//        user.setNgaySua(LocalDate.now());
//        serUser.registerUser(user);
//
//        return "redirect:/TTAP/User/home";
//    }
//
//    @PostMapping("/createNv")
//    public String registerNv(
//            @Valid @ModelAttribute("user") User user, BindingResult result,
//            Model model, RedirectAttributes redirectAttributes
//    ) {
//        if (result.hasErrors()) {
//            model.addAttribute("cvList",serCv.findAll());
//            return "user/register"; // Trả về lại form nếu có lỗi
//        }
//
//        user.setNgayTao(LocalDate.now());
//        user.setNgaySua(LocalDate.now());
//        serUser.registerUser(user);
//
//        return "redirect:/TTAP/User/employee";
//    }
//
//    @PostMapping("/createKh")
//    public String registerKh(
//            @Valid @ModelAttribute("user") User user, BindingResult result,
//            Model model, RedirectAttributes redirectAttributes
//    ) {
//        if (result.hasErrors()) {
//            model.addAttribute("cvList",serCv.findAll());
//            return "user/register"; // Trả về lại form nếu có lỗi
//        }
//
//        user.setNgayTao(LocalDate.now());
//        user.setNgaySua(LocalDate.now());
//        serUser.registerUser(user);
//
//        return "redirect:/TTAP/User/customer";
//    }
//
//}
