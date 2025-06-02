using EduconnectAPI.Config;
using EduconnectAPI.Controllers;
using EduconnectAPI.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Xunit;
using Task = System.Threading.Tasks.Task;

namespace EduconnectAPI.EduconnectAPI.Tests;

public class TaskControllerTests
{
    private readonly ApplicationDbContext _context;
    private readonly TaskController _controller;

    public TaskControllerTests()
    {
        var options = new DbContextOptionsBuilder<ApplicationDbContext>()
            .UseInMemoryDatabase(Guid.NewGuid().ToString())
            .Options;

        _context = new ApplicationDbContext(options);
        _controller = new TaskController(_context);
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

    private global::EduconnectAPI.Models.Task CreateTestTask(int id = 1, int courseId = 1)
    {
        return new global::EduconnectAPI.Models.Task
        {
            Id = id,
            Title = "Test Task",
            Description = "Test Description",
            DueDate = DateTime.UtcNow.AddDays(7),
            CourseId = courseId
        };
    }

    [Fact]
    public async Task GetAllTasks_ReturnsOk_WithTasks()
    {
        var course = CreateTestCourse();
        _context.Courses.Add(course);
        _context.Tasks.Add(CreateTestTask(1, course.Id));
        await _context.SaveChangesAsync();

        var result = await _controller.GetAllTasks();

        var okResult = Assert.IsType<OkObjectResult>(result);
        var tasks = Assert.IsType<List<global::EduconnectAPI.Models.Task>>(okResult.Value);
        Assert.Single(tasks);
    }

    [Fact]
    public async Task GetTaskById_ReturnsOk_WhenTaskExists()
    {
        var course = CreateTestCourse();
        _context.Courses.Add(course);
        var task = CreateTestTask(1, course.Id);
        _context.Tasks.Add(task);
        await _context.SaveChangesAsync();

        var result = await _controller.GetTaskById(1);

        var okResult = Assert.IsType<OkObjectResult>(result);
        var returnedTask = Assert.IsType<global::EduconnectAPI.Models.Task>(okResult.Value);
        Assert.Equal(task.Title, returnedTask.Title);
    }

    [Fact]
    public async Task GetTaskById_ReturnsNotFound_WhenTaskDoesNotExist()
    {
        var result = await _controller.GetTaskById(999);
        var notFound = Assert.IsType<NotFoundObjectResult>(result);
        Assert.Equal("La tarea no existe.", notFound.Value);
    }

    [Fact]
    public async Task CreateTask_ReturnsCreatedAtActionResult()
    {
        var course = CreateTestCourse();
        _context.Courses.Add(course);
        await _context.SaveChangesAsync();

        var task = new global::EduconnectAPI.Models.Task
        {
            Title = "Nueva tarea",
            Description = "Descripción",
            DueDate = DateTime.UtcNow.AddDays(3),
            CourseId = course.Id
        };

        var result = await _controller.CreateTask(task);

        var createdResult = Assert.IsType<CreatedAtActionResult>(result);
        var createdTask = Assert.IsType<global::EduconnectAPI.Models.Task>(createdResult.Value);
        Assert.Equal("Nueva tarea", createdTask.Title);
    }

    [Fact]
    public async Task UpdateTask_ReturnsOk_WhenTaskExists()
    {
        var course = CreateTestCourse();
        _context.Courses.Add(course);
        var task = CreateTestTask(1, course.Id);
        _context.Tasks.Add(task);
        await _context.SaveChangesAsync();

        var updatedTask = new global::EduconnectAPI.Models.Task
        {
            Title = "Actualizada",
            Description = "Nueva descripción",
            DueDate = DateTime.UtcNow.AddDays(10),
            CourseId = course.Id
        };

        var result = await _controller.UpdateTask(1, updatedTask);

        var okResult = Assert.IsType<OkObjectResult>(result);
        Assert.Equal("La tarea fue actualizada exitosamente.", okResult.Value);

        var dbTask = await _context.Tasks.FindAsync(1);
        Assert.Equal("Actualizada", dbTask.Title);
    }

    [Fact]
    public async Task UpdateTask_ReturnsNotFound_WhenTaskDoesNotExist()
    {
        var updatedTask = new global::EduconnectAPI.Models.Task
        {
            Title = "Actualizada"
        };

        var result = await _controller.UpdateTask(999, updatedTask);
        var notFound = Assert.IsType<NotFoundObjectResult>(result);
        Assert.Equal("La tarea no existe.", notFound.Value);
    }

    [Fact]
    public async Task DeleteTask_ReturnsOk_WhenTaskExists()
    {
        var course = CreateTestCourse();
        _context.Courses.Add(course);
        var task = CreateTestTask(1, course.Id);
        _context.Tasks.Add(task);
        await _context.SaveChangesAsync();

        var result = await _controller.DeleteTask(1);

        var okResult = Assert.IsType<OkObjectResult>(result);
        Assert.Equal("La tarea fue eliminada exitosamente.", okResult.Value);
    }

    [Fact]
    public async Task DeleteTask_ReturnsNotFound_WhenTaskDoesNotExist()
    {
        var result = await _controller.DeleteTask(999);
        var notFound = Assert.IsType<NotFoundObjectResult>(result);
        Assert.Equal("La tarea no existe.", notFound.Value);
    }
}