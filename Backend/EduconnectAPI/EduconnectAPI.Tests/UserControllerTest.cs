using EduconnectAPI.Config;
using EduconnectAPI.Controllers;
using EduconnectAPI.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Xunit;
using Task = System.Threading.Tasks.Task;

namespace EduconnectAPI.EduconnectAPI.Tests;

public class UserControllerTests
{
    private readonly ApplicationDbContext _context;
    private readonly UserController _controller;

    public UserControllerTests()
    {
        var options = new DbContextOptionsBuilder<ApplicationDbContext>()
            .UseInMemoryDatabase(Guid.NewGuid().ToString())
            .Options;

        _context = new ApplicationDbContext(options);
        _controller = new UserController(_context);
    }

    private User CreateTestUser(string id = "1")
    {
        return new User
        {
            Id = id,
            Name = "Test User",
            Email = "test@example.com",
            PasswordHash = "hash",
            Role = "Student",
            CreateAt = DateTime.UtcNow
        };
    }

    [Fact]
    public async Task GetAllUsers_ReturnsOk_WithUsers()
    {
        _context.Users.Add(CreateTestUser("1"));
        await _context.SaveChangesAsync();

        var result = await _controller.GetAllUsers();

        var okResult = Assert.IsType<OkObjectResult>(result);
        var users = Assert.IsType<List<User>>(okResult.Value);
        Assert.Single(users);
    }

    [Fact]
    public async Task GetUserById_ReturnsOk_WhenUserExists()
    {
        var user = CreateTestUser("1");
        _context.Users.Add(user);
        await _context.SaveChangesAsync();

        var result = await _controller.GetUserById("1");

        var okResult = Assert.IsType<OkObjectResult>(result);
        var returnedUser = Assert.IsType<User>(okResult.Value);
        Assert.Equal(user.Name, returnedUser.Name);
    }

    [Fact]
    public async Task GetUserById_ReturnsNotFound_WhenUserDoesNotExist()
    {
        var result = await _controller.GetUserById("999");
        var notFound = Assert.IsType<NotFoundObjectResult>(result);
        Assert.Equal("El usuario no existe.", notFound.Value);
    }

    [Fact]
    public async Task UpdateUser_ReturnsOk_WhenUserExists()
    {
        var user = CreateTestUser("1");
        _context.Users.Add(user);
        await _context.SaveChangesAsync();

        var updatedUser = new User
        {
            Name = "Nuevo Nombre",
            Email = "nuevo@email.com",
            Role = "Admin"
        };

        var result = await _controller.UpdateUser("1", updatedUser);

        var okResult = Assert.IsType<OkObjectResult>(result);
        Assert.Equal("El usuario fue actualizado exitosamente.", okResult.Value);

        var dbUser = await _context.Users.FindAsync("1");
        Assert.Equal("Nuevo Nombre", dbUser.Name);
        Assert.Equal("Admin", dbUser.Role);
    }

    [Fact]
    public async Task UpdateUser_ReturnsNotFound_WhenUserDoesNotExist()
    {
        var updatedUser = new User { Name = "Nuevo" };
        var result = await _controller.UpdateUser("999", updatedUser);
        var notFound = Assert.IsType<NotFoundObjectResult>(result);
        Assert.Equal("El usuario no existe.", notFound.Value);
    }

    [Fact]
    public async Task DeleteUser_ReturnsOk_WhenUserExists()
    {
        var user = CreateTestUser("1");
        _context.Users.Add(user);
        await _context.SaveChangesAsync();

        var result = await _controller.DeleteUser("1");

        var okResult = Assert.IsType<OkObjectResult>(result);
        Assert.Equal("El usuario fue eliminado exitosamente.", okResult.Value);
        Assert.Empty(_context.Users);
    }

    [Fact]
    public async Task DeleteUser_ReturnsNotFound_WhenUserDoesNotExist()
    {
        var result = await _controller.DeleteUser("999");
        var notFound = Assert.IsType<NotFoundObjectResult>(result);
        Assert.Equal("El usuario no existe.", notFound.Value);
    }
}