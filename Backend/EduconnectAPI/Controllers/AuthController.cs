using EduconnectAPI.Config;
using EduconnectAPI.Models;
using EduconnectAPI.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Security.Cryptography;
using System.Text;

namespace EduconnectAPI.Controllers
{
    [ApiController]
    [Route("api/v1/[controller]")]
    public class AuthController : ControllerBase
    {
        private readonly ApplicationDbContext _context;
        private readonly JwtService _jwtService;
        private readonly string _hmacKey;

        public AuthController(ApplicationDbContext context, JwtService jwtService, IConfiguration configuration)
        {
            _context = context;
            _jwtService = jwtService;
            _hmacKey = configuration["HmacKey"];
        }

        [HttpPost("register")]
        public async Task<IActionResult> Register([FromBody] User user)
        {
            if (await _context.Users.AnyAsync(u => u.Email == user.Email))
            {
                return BadRequest("Email already in use");
            }

            using var hmac = new HMACSHA256(Encoding.UTF8.GetBytes(_hmacKey));
            user.PasswordHash = Convert.ToBase64String(hmac.ComputeHash(Encoding.UTF8.GetBytes(user.PasswordHash)));
            user.CreateAt = DateTime.UtcNow;

            _context.Users.Add(user);
            await _context.SaveChangesAsync();

            return Created("User registered successfully", user);
        }

        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] User user)
        {
            var dbUser = await _context.Users.FirstOrDefaultAsync(u => u.Email == user.Email);
            if (dbUser == null)
            {
                return BadRequest("Invalid email or password");
            }

            using var hmac = new HMACSHA256(Encoding.UTF8.GetBytes(_hmacKey));
            var passwordHash = Convert.ToBase64String(hmac.ComputeHash(Encoding.UTF8.GetBytes(user.PasswordHash)));

            Console.WriteLine(passwordHash);
            Console.WriteLine(dbUser.PasswordHash);
            if (dbUser.PasswordHash != passwordHash)
            {
                return BadRequest("Invalid email or password");
            }

            var token = _jwtService.GenerateToken(dbUser.Email, dbUser.Role);
            return Ok(new { token });
        }
    }
}
