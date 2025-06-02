using EduconnectAPI.Config;
using EduconnectAPI.Models;
using EduconnectAPI.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Security.Cryptography;
using System.Text;

namespace EduconnectAPI.Controllers
{
    /// <summary>
    /// Controller responsible for authentication operations such as registration and login.
    /// </summary>
    [ApiController]
    [Route("api/v1/[controller]")]
    public class AuthController : ControllerBase
    {
        private readonly ApplicationDbContext _context;
        private readonly JwtService _jwtService;
        private readonly string _hmacKey;

        /// <summary>
        /// Initializes a new instance of the <see cref="AuthController"/> class.
        /// </summary>
        /// <param name="context">The application database context.</param>
        /// <param name="jwtService">Service for generating JWT tokens.</param>
        /// <param name="configuration">Application configuration for retrieving the HMAC key.</param>
        public AuthController(ApplicationDbContext context, JwtService jwtService, IConfiguration configuration)
        {
            _context = context;
            _jwtService = jwtService;
            _hmacKey = configuration["HmacKey"];
        }

        /// <summary>
        /// Registers a new user in the system.
        /// </summary>
        /// <param name="user">The user object containing registration data.</param>
        /// <returns>
        /// Returns 201 if registration is successful, or 400 if the email or id is already in use.
        /// </returns>
        [HttpPost("register")]
        public async Task<IActionResult> Register([FromBody] User user)
        {
            if (await _context.Users.AnyAsync(u => u.Email == user.Email || u.Id == user.Id))
            {
                return BadRequest("Email or id  already in use");
            }

            using var hmac = new HMACSHA256(Encoding.UTF8.GetBytes(_hmacKey));
            user.PasswordHash = Convert.ToBase64String(hmac.ComputeHash(Encoding.UTF8.GetBytes(user.PasswordHash)));
            user.CreateAt = DateTime.UtcNow;

            _context.Users.Add(user);
            await _context.SaveChangesAsync();

            return Created("User registered successfully", user);
        }

        /// <summary>
        /// Authenticates a user and returns a JWT token if credentials are valid.
        /// </summary>
        /// <param name="user">The user DTO containing login credentials.</param>
        /// <returns>
        /// Returns 200 with a JWT token if successful, or 400 if credentials are invalid or the user is not allowed.
        /// </returns>
        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] UserDto user)
        {
            var dbUser = await _context.Users.FirstOrDefaultAsync(u => u.Id == user.Id);
            if (dbUser == null)
            {
                return BadRequest("Invalid id");
            }

            using var hmac = new HMACSHA256(Encoding.UTF8.GetBytes(_hmacKey));
            var passwordHash = Convert.ToBase64String(hmac.ComputeHash(Encoding.UTF8.GetBytes(user.Password)));

            Console.WriteLine(passwordHash);
            Console.WriteLine(dbUser.PasswordHash);
            if (dbUser.PasswordHash != passwordHash)
            {
                return BadRequest("Invalid password");
            }

            if (dbUser.Role != user.Role && user.Role != "Admin")
            {
                return BadRequest("The user is not allowed to login as " + user.Role + "");
            }

            var token = _jwtService.GenerateToken(dbUser.Id, dbUser.Role);
            return Ok(new { token });
        }
    }
}