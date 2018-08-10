// Programmer: Mihalis Tsoukalos
// Date: Wednesday 04 June 2014
//
// A simple OpenGL program that draws a triangle.

#include "GL/freeglut.h"
#include "GL/gl.h"

// Rotate X
double rX=0;
// Rotate Y
double rY=0;

void draw()
{
    glClearColor(0.4, 0.4, 0.4, 0.4);
    glClear(GL_COLOR_BUFFER_BIT);

    glLoadIdentity();

    glRotatef( rX, 1.0, 0.0, 0.0 );
	glRotatef( rY, 0.0, 1.0, 0.0 );

		glColor3f(1.0, 1.0, 1.0);
		glEnable(GL_POINT_SMOOTH);
		glPointSize(5);

		glBegin(GL_POINTS);
			glVertex3f(0.25, 0.25, 0);
			glVertex3f(0.45, 0.25, 0);
			glVertex3f(0.25, 0.45, 0);
			glVertex3f(0.45, 0.45, 0);

			glVertex3f(0.25, 0.25, -0.2);
			glVertex3f(0.45, 0.25, -0.2);
			glVertex3f(0.25, 0.45, -0.2);
			glVertex3f(0.45, 0.45, -0.2);
		glEnd();

		glBegin(GL_LINES);
		//Lineas desde X paralelas a Z

			glVertex3f(0, 0, 0);
			glVertex3f(0.72, 0, 0);
			glVertex3f(0, 0, 0.08);
			glVertex3f(0.72, 0, 0.08);
			glVertex3f(0, 0, 0.16);
			glVertex3f(0.72, 0, 0.16);
			glVertex3f(0, 0, 0.24);
			glVertex3f(0.72, 0, 0.24);
			glVertex3f(0, 0, 0.32);
			glVertex3f(0.72, 0, 0.32);
			glVertex3f(0, 0, 0.4);
			glVertex3f(0.72, 0, 0.4);
			glVertex3f(0, 0, 0.48);
			glVertex3f(0.72, 0, 0.48);
			glVertex3f(0, 0, 0.56);
			glVertex3f(0.72, 0, 0.56);
			glVertex3f(0, 0, 0.64);
			glVertex3f(0.72, 0, 0.64);
			glVertex3f(0, 0, 0.72);
			glVertex3f(0.72, 0, 0.72);

		//Lineas desde Z paralelas a X

			glVertex3f(0, 0, 0);
			glVertex3f(0, 0, 0.72);
			glVertex3f(0.08, 0, 0);
			glVertex3f(0.08, 0, 0.72);
			glVertex3f(0.16, 0, 0);
			glVertex3f(0.16, 0, 0.72);
			glVertex3f(0.24, 0, 0);
			glVertex3f(0.24, 0, 0.72);
			glVertex3f(0.32, 0, 0);
			glVertex3f(0.32, 0, 0.72);
			glVertex3f(0.4, 0, 0);
			glVertex3f(0.4, 0, 0.72);
			glVertex3f(0.48, 0, 0);
			glVertex3f(0.48, 0, 0.72);
			glVertex3f(0.56, 0, 0);
			glVertex3f(0.56, 0, 0.72);
			glVertex3f(0.64, 0, 0);
			glVertex3f(0.64, 0, 0.72);
			glVertex3f(0.72, 0, 0);
			glVertex3f(0.72, 0, 0.72);

			// Lineas de las coordenadas

			glColor3f(1.0, 0, 0);
				glVertex3f(0, 0, 0);
				glVertex3f(0.5, 0, 0);

			glColor3f(0, 1.0, 0);
				glVertex3f(0, 0, 0);
				glVertex3f(0, 0.5, 0);

			glColor3f(0, 0, 1.0);
				glVertex3f(0, 0, 0);
				glVertex3f(0, 0, 0.5);
		glEnd();

    glFlush();
}


void keyboard(int key, int x, int y)
{
    if (key == GLUT_KEY_RIGHT)
        {
                rY += 15;
        }
    else if (key == GLUT_KEY_LEFT)
        {
                rY -= 15;
        }
    else if (key == GLUT_KEY_DOWN)
        {
                rX -= 15;
        }
    else if (key == GLUT_KEY_UP)
        {
                rX += 15;
        }

    // Request display update
    glutPostRedisplay();
}

int main(int argc, char **argv)
{
	//Move the window content with the cursor keys
    glutInit(&argc, argv);
    glutInitDisplayMode(GLUT_SINGLE);
    glutInitWindowSize(500, 500);
    glutInitWindowPosition(100, 100);
    glutCreateWindow("coordinates - grid - cube vertices");
    glutDisplayFunc(draw);
    glutSpecialFunc(keyboard);
    glutMainLoop();
    return 0;
}
