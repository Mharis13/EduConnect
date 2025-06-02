using EduconnectAPI.Config;
using EduconnectAPI.Controllers;
using EduconnectAPI.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Xunit;
using Task = System.Threading.Tasks.Task;

namespace EduconnectAPI.EduconnectAPI.Tests;

public class CourseControllerTests
{
    private readonly ApplicationDbContext _context;
    private readonly CourseController _controller;

    public CourseControllerTests()
    {
        var options = new DbContextOptionsBuilder<ApplicationDbContext>()
            .UseInMemoryDatabase(Guid.NewGuid().ToString()) // Base de datos única por test
            .Options;

        _context = new ApplicationDbContext(options);
        _controller = new CourseController(_context);
    }

    private User CreateTestUser(string id = "1")
    {
        return new User
        {
            Id = id,
            Name = "Test User",
            Email = "test@example.com",
            PasswordHash = "hash",
            Role = "Teacher",
            CreateAt = DateTime.UtcNow
        };
    }

    private Course CreateTestCourse(int id = 1, string creatorId = "1")
    {
        return new Course
        {
            Id = id,
            Name = "Test Course",
            Description = "Test Description",
            CreatorId = creatorId,
            CreadedAt = DateTime.UtcNow
        };
    }

    [Fact]
    public async Task GetCourses_ReturnsOkResult_WithCourses()
    {
        var user = CreateTestUser();
        _context.Users.Add(user);
        _context.Courses.Add(CreateTestCourse(1, user.Id));
        await _context.SaveChangesAsync();

        var result = await _controller.GetCourses();

        var okResult = Assert.IsType<OkObjectResult>(result);
        var courses = Assert.IsType<List<Course>>(okResult.Value);
        Assert.Single(courses);
    }

    [Fact]
    public async Task GetCourseById_ReturnsNotFound_WhenCourseDoesNotExist()
    {
        var result = await _controller.GetCourseById(999);
        Assert.IsType<NotFoundResult>(result);
    }

    [Fact]
    public async Task GetCourseById_ReturnsOkResult_WhenCourseExists()
    {
        var user = CreateTestUser();
        _context.Users.Add(user);
        var course = CreateTestCourse(1, user.Id);
        _context.Courses.Add(course);
        await _context.SaveChangesAsync();

        var result = await _controller.GetCourseById(1);

        var okResult = Assert.IsType<OkObjectResult>(result);
        var returnedCourse = Assert.IsType<Course>(okResult.Value);
        Assert.Equal(course.Name, returnedCourse.Name);
    }

    [Fact]
    public async Task CreateCourse_ReturnsCreatedAtActionResult()
    {
        var user = CreateTestUser();
        _context.Users.Add(user);
        await _context.SaveChangesAsync();
        var course = new CourseDto()
        {
            Name = "New Course",
            Description = "New Description",
            
        };

        var result = await _controller.CreateCourse(course);

        var createdResult = Assert.IsType<CreatedAtActionResult>(result);
        var createdCourse = Assert.IsType<Course>(createdResult.Value);
        Assert.Equal("New Course", createdCourse.Name);
    }

    [Fact]
    public async Task DeleteCourseById_ReturnsNotFound_WhenCourseDoesNotExist()
    {
        var result = await _controller.DeleteCourseById(999);
        Assert.IsType<NotFoundResult>(result);
    }

    [Fact]
    public async Task DeleteCourseById_ReturnsNoContent_WhenCourseExists()
    {
        var user = CreateTestUser();
        _context.Users.Add(user);
        var course = CreateTestCourse(1, user.Id);
        _context.Courses.Add(course);
        await _context.SaveChangesAsync();

        var result = await _controller.DeleteCourseById(1);

        Assert.IsType<NoContentResult>(result);
    }

    [Fact]
    public async Task GetStudentsInCourse_ReturnsNotFound_WhenCourseDoesNotExist()
    {
        var result = await _controller.GetStudentsInCourse(999);
        Assert.IsType<NotFoundObjectResult>(result);
    }

    [Fact]
    public async Task AddStudentsToCourse_ReturnsOkResult()
    {
        var user = CreateTestUser();
        _context.Users.Add(user);
        var course = CreateTestCourse(1, user.Id);
        _context.Courses.Add(course);
        await _context.SaveChangesAsync();

        var result = await _controller.AddStudentsToCourse(1, new List<string> { user.Id });

        Assert.IsType<OkObjectResult>(result);
    }

    [Fact]
    public async Task DeleteStudentFromCourse_ReturnsOkResult()
    {
        var user = CreateTestUser();
        _context.Users.Add(user);
        var course = CreateTestCourse(1, user.Id);
        _context.Courses.Add(course);
        var enrollment = new Enrollment { CourseId = 1, UserId = user.Id };
        _context.Enrollments.Add(enrollment);
        await _context.SaveChangesAsync();

        var result = await _controller.DeleteStudentFromCourse(1, new List<string> { user.Id });

        Assert.IsType<OkObjectResult>(result);
    }
}