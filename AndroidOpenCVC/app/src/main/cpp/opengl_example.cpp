//
// Created by fjpalfer on 16/08/18.
//

#include "opengl_example.h"
#include "glwrapper.h"

void on_surface_created() {
    glClearColor(0.0f, 1.0f, 0.0f, 0.0f);
}

void on_surface_changed() {
    // No-op
}

void on_draw_frame() {
    glClear(GL_COLOR_BUFFER_BIT);
}
