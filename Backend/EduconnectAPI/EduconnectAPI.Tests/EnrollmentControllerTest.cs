using EduconnectAPI.Config;
using EduconnectAPI.Controllers;
using EduconnectAPI.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Xunit;
using Task = System.Threading.Tasks.Task;

namespace EduconnectAPI.EduconnectAPI.Tests;

public class EnrollmentControllerTests
{
    private readonly ApplicationDbContext _context;
    private readonly EnrollmentController _controller;

    public EnrollmentControllerTests()
    {
        var options = new DbContextOptionsBuilder<ApplicationDbContext>()
            .UseInMemoryDatabase(Guid.NewGuid().ToString())
            .Options;

        _context = new ApplicationDbContext(options);
        _controller = new EnrollmentController(_context);
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
    public async Task GetAllEnrollments_ReturnsOkResult_WithEnrollments()
    {
        var user = CreateTestUser();
        var course = CreateTestCourse(1, user.Id);
        _context.Users.Add(user);
        _context.Courses.Add(course);
        var enrollment = new Enrollment { CourseId = course.Id, UserId = user.Id };
        _context.Enrollments.Add(enrollment);
        await _context.SaveChangesAsync();

        var result = await _controller.GetAllEnrollments();

        var okResult = Assert.IsType<OkObjectResult>(result);
        var enrollments = Assert.IsType<List<Enrollment>>(okResult.Value);
        Assert.Single(enrollments);
    }

    [Fact]
    public async Task EnrollStudent_ReturnsOkResult()
    {
        var user = CreateTestUser();
        var course = CreateTestCourse(1, user.Id);
        _context.Users.Add(user);
        _context.Courses.Add(course);
        await _context.SaveChangesAsync();

        var enrollment = new Enrollment { CourseId = course.Id, UserId = user.Id };

        var result = await _controller.EnrollStudent(enrollment);

        var okResult = Assert.IsType<OkObjectResult>(result);
        Assert.Equal("El estudiante fue inscrito exitosamente.", okResult.Value);
    }

    [Fact]
    public async Task RemoveEnrollment_ReturnsNotFound_WhenEnrollmentDoesNotExist()
    {
        var result = await _controller.RemoveEnrollment(999);
        var notFound = Assert.IsType<NotFoundObjectResult>(result);
        Assert.Equal("La inscripción no existe.", notFound.Value);
    }

    [Fact]
    public async Task RemoveEnrollment_ReturnsOkResult_WhenEnrollmentExists()
    {
        var user = CreateTestUser();
        var course = CreateTestCourse(1, user.Id);
        _context.Users.Add(user);
        _context.Courses.Add(course);
        var enrollment = new Enrollment { CourseId = course.Id, UserId = user.Id };
        _context.Enrollments.Add(enrollment);
        await _context.SaveChangesAsync();

        var result = await _controller.RemoveEnrollment(enrollment.Id);

        var okResult = Assert.IsType<OkObjectResult>(result);
        Assert.Equal("La inscripción fue eliminada exitosamente.", okResult.Value);
    }
}